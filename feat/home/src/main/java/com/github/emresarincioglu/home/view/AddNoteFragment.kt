package com.github.emresarincioglu.home.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.emresarincioglu.home.R
import com.github.emresarincioglu.home.databinding.FragmentAddNoteBinding
import com.github.emresarincioglu.home.viewmodel.AddNoteViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding: FragmentAddNoteBinding get() = _binding!!

    private val addNoteViewModel by viewModels<AddNoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {

            val surfaceColor = MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorSurface,
                Color.TRANSPARENT
            )
            startContainerColor = surfaceColor
            endContainerColor = surfaceColor
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddNoteBinding.inflate(inflater, container, false).apply {
            viewModel = addNoteViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupViews()

        return binding.root
    }

    private fun setupViews() {


        binding.appBar.setNavigationOnClickListener {

            if (binding.etNoteTitle.hasFocus()) {
                binding.etNoteTitle.clearFocus()
            } else {
                binding.etNoteContent.clearFocus()
            }

            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            val isEdited =
                binding.etNoteTitle.text.isNotEmpty() || binding.etNoteContent.text.isNotEmpty()
            if (isEdited) {
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

        binding.appBar.setOnMenuItemClickListener { menuItem ->

            if (menuItem.itemId == R.id.menu_item_save) {
                TODO("Add note to database")
                true
            } else false
        }
    }
}