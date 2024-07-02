package com.example.securenotes.feat.home.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.home.R
import com.example.securenotes.feat.home.databinding.FragmentAddNoteBinding
import com.example.securenotes.feat.home.viewmodel.AddNoteViewModel
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform

class AddNoteFragment : DataBindingFragment<FragmentAddNoteBinding>() {
    private val addNoteViewModel by viewModels<AddNoteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            MaterialContainerTransform().apply {
                setPathMotion(MaterialArcMotion())
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
        inflateBinding(R.layout.fragment_add_note, inflater, container, false)
        binding.viewModel = addNoteViewModel

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToHomeScreen()
        }

        setupViews()
        return binding.root
    }

    private fun setupViews() {
        binding.appBar.setNavigationOnClickListener {
            navigateToHomeScreen()
        }

        binding.appBar.setOnMenuItemClickListener { menuItem ->

            if (menuItem.itemId == R.id.menu_item_save) {
                TODO("Add note to database")
                true
            } else {
                false
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
}
