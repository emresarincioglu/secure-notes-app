package com.example.securenotes.feat.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.securenotes.core.common.Result
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.home.R
import com.example.securenotes.feat.home.databinding.FragmentNoteBinding
import com.example.securenotes.feat.home.viewmodel.NoteViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : DataBindingFragment<FragmentNoteBinding>() {

    private val onTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable?) = noteViewModel.setIsEdited(true)
    }

    private var lastScrollPosition = 0
    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(
                MaterialColors.getColor(
                    requireContext(),
                    com.google.android.material.R.attr.colorSurface,
                    Color.TRANSPARENT
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflateBinding(R.layout.fragment_note, inflater, container, false)
        binding.viewModel = noteViewModel

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToHomeScreen()
        }

        binding.etNoteContent.setOnScrollChangeListener { view, _, scrollY, _, _ ->
            val result = noteViewModel.getNoteContentResultStream.value
            val isNoteLoadedCompletely = result == Result.Success(null)
            val offset = binding.etNoteContent.layout.height - view.height - scrollY

            if (result != Result.Loading && !isNoteLoadedCompletely && offset < 700) {
                lastScrollPosition = binding.etNoteContent.selectionEnd
                noteViewModel.getMoreNoteContent()
            }
        }

        setupViews()
        observeActionResults()

        return binding.root
    }

    private fun setupViews() {
        val args = requireArguments()
        val noteId = args.getInt("note_id")
        val noteTitle = args.getString("note_title")!!
        val noteContent = args.getString("note_content")!!

        noteViewModel.setNoteId(noteId)
        with(noteViewModel.uiState.value) {
            title = noteTitle
            content = noteContent
        }
        binding.executePendingBindings()
        noteViewModel.getMoreNoteContent()

        binding.etNoteTitle.addTextChangedListener(onTextChangedListener)
        binding.etNoteContent.addTextChangedListener(onTextChangedListener)

        binding.piNote.setVisibilityAfterHide(View.GONE)

        setupAppBar()
    }

    @SuppressLint("SetTextI18n")
    private fun observeActionResults() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    noteViewModel.getNoteContentResultStream.collect { result ->
                        when (result) {
                            Result.Loading -> binding.piNote.show()

                            is Result.Success -> {
                                binding.piNote.hide()
                                binding.etNoteContent.apply {
                                    removeTextChangedListener(onTextChangedListener)

                                    setText("$text${result.data.orEmpty()}")
                                    setSelection(lastScrollPosition)

                                    addTextChangedListener(onTextChangedListener)
                                }
                            }

                            is Result.Error -> {
                                binding.piNote.hide()
                                throw result.exception
                            }

                            else -> Unit
                        }
                    }
                }

                launch {
                    noteViewModel.updateNoteResultStream.collect { result ->
                        when (result) {
                            Result.Success(true) -> {
                                Toast.makeText(
                                    context,
                                    R.string.toast_note_saved,
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().popBackStack()
                            }

                            is Result.Error -> Toast.makeText(
                                context,
                                R.string.error_note_not_saved,
                                Toast.LENGTH_SHORT
                            ).show()

                            else -> Unit
                        }
                    }
                }

                noteViewModel.deleteNoteResultStream.collect { result ->
                    when (result) {
                        Result.Success(true) -> {
                            Toast.makeText(
                                context,
                                R.string.toast_note_deleted,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }

                        is Result.Error -> Toast.makeText(
                            context,
                            getString(R.string.error_note_not_deleted),
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupAppBar() {
        binding.appBar.setNavigationOnClickListener {
            navigateToHomeScreen()
        }

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_save -> {
                    if (noteViewModel.uiState.value.isEdited) {
                        noteViewModel.updateNote()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.toast_note_not_edited,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }

                R.id.menu_item_delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_note_title))
                        .setMessage(getString(R.string.dialog_delete_note_message))
                        .setNegativeButton(resources.getString(android.R.string.cancel), null)
                        .setPositiveButton(
                            resources.getString(android.R.string.ok)
                        ) { dialog, which ->
                            noteViewModel.deleteNote()
                        }
                        .show()
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToHomeScreen() {
        activity?.currentFocus?.clearFocus()

        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

        warnAndPopBack()
    }

    private fun warnAndPopBack() {
        if (noteViewModel.uiState.value.isEdited) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_discard_note_title))
                .setMessage(getString(R.string.dialog_discard_note_message))
                .setNegativeButton(resources.getString(android.R.string.cancel), null)
                .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                    findNavController().popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }
}
