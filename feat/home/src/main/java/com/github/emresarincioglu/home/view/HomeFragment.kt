package com.github.emresarincioglu.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
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

        setupRecyclerViews()
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

    private fun setupRecyclerViews() {

        val swapNotesCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ): Int {

                val adapter = recyclerView.adapter as NoteRecyclerViewAdapter
                val itemPosition = viewHolder.adapterPosition

                return if (adapter.isNote(itemPosition)) {
                    makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
                } else {
                    makeMovementFlags(0, 0)
                }
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val adapter = recyclerView.adapter as NoteRecyclerViewAdapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                return if (adapter.isNote(from) && adapter.isNote(to)) {
                    homeViewModel.swapNotes(from, to)
                    adapter.notifyItemMoved(from, to)
                    true
                } else false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    val alphaAnimation = AlphaAnimation(1f, 0.5f).apply {
                        duration = 250
                        fillAfter = true
                    }
                    viewHolder?.itemView?.startAnimation(alphaAnimation)
                }
            }

            override fun clearView(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                val alphaAnimation = AlphaAnimation(0.5f, 1f).apply {
                    duration = 350
                    fillAfter = true
                }
                viewHolder.itemView.startAnimation(alphaAnimation)
            }
        }
        ItemTouchHelper(swapNotesCallback).attachToRecyclerView(binding.rvNote)
    }
}