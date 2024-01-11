package com.github.emresarincioglu.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.emresarincioglu.home.NoteRecyclerViewAdapter
import com.github.emresarincioglu.home.databinding.FragmentHomeBinding
import com.github.emresarincioglu.home.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupViews()
        observeUiState()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {

        binding.fabNote.setOnClickListener {
            // TODO: Navigate to new note screen
        }
    }

    private fun observeUiState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                homeViewModel.uiState.collectLatest { uiState ->
                    binding.rvNote.adapter = NoteRecyclerViewAdapter(
                        notes = uiState.notes,
                        showWarning = !uiState.isCreatedPassword,
                        onNoteClick = { note ->
                            // TODO: Navigate to note screen
                        },
                        onWarningClick = {
                            // TODO: Show biometric prompt
                        }
                    )
                }
            }
        }
    }
}