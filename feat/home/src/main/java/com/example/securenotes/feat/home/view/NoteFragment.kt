package com.example.securenotes.feat.home.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.home.R
import com.example.securenotes.feat.home.databinding.FragmentNoteBinding
import com.example.securenotes.feat.home.viewmodel.NoteViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform

class NoteFragment : DataBindingFragment<FragmentNoteBinding>() {
    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            MaterialContainerTransform().apply {
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToHomeScreen()
        }

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        val args = requireArguments()
        val noteId = args.getInt(getString(R.string.fragment_note_arg_note_id))
        val noteTitle = args.getString(getString(R.string.fragment_note_arg_note_title))!!
        val noteContent = args.getString(getString(R.string.fragment_note_arg_note_content))!!

        noteViewModel.setNoteId(noteId)
        with(noteViewModel.uiState.value) {
            title = noteTitle
            content = noteContent
        }
        binding.executePendingBindings()

        binding.etNoteTitle.doAfterTextChanged {
            noteViewModel.setIsEdited(true)
        }
        binding.etNoteContent.doAfterTextChanged {
            noteViewModel.setIsEdited(true)
        }

        setupAppBar()
    }

    private fun setupAppBar() {
        binding.appBar.setNavigationOnClickListener {
            navigateToHomeScreen()
        }

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_save -> {
                    TODO("Save changes to database")
                    true
                }

                R.id.menu_item_delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_note_title))
                        .setMessage(getString(R.string.dialog_delete_note_message))
                        .setNegativeButton(resources.getString(android.R.string.cancel), null)
                        .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                            TODO("Delete note from database")
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

        warnAndNavigateUp()
    }

    private fun warnAndNavigateUp() {
        if (noteViewModel.uiState.value.isEdited) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_discard_note_title))
                .setMessage(getString(R.string.dialog_discard_note_message))
                .setNegativeButton(resources.getString(android.R.string.cancel), null)
                .setPositiveButton(resources.getString(android.R.string.ok)) { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        } else {
            findNavController().navigateUp()
        }
    }
}
