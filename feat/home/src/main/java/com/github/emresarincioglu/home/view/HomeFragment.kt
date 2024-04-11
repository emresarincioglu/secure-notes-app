package com.github.emresarincioglu.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.emresarincioglu.home.DataBindingFragment
import com.github.emresarincioglu.home.NoteRecyclerViewAdapter
import com.github.emresarincioglu.home.R
import com.github.emresarincioglu.home.SearchResultRecyclerViewAdapter
import com.github.emresarincioglu.home.databinding.FragmentHomeBinding
import com.github.emresarincioglu.home.viewmodel.HomeViewModel
import com.google.android.material.search.SearchView
import com.google.android.material.transition.Hold
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : DataBindingFragment<FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val args by navArgs<HomeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        inflateBinding(R.layout.fragment_home, inflater, container, false)
        binding.viewModel = homeViewModel

        setupViews()
        observeUiState()

        return binding.root
    }

    private fun setupViews() {

        setupNoteRecyclerView()
        setupNoteSearchView()

        binding.fabNote.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAddNoteFragment(),
                FragmentNavigatorExtras(binding.fabNote to binding.fabNote.transitionName)
            )
        }
    }

    private fun observeUiState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collectLatest { uiState ->

                    binding.rvNote.adapter = NoteRecyclerViewAdapter(
                        notes = uiState.notes,
                        showWarning = !args.isPasswordCreated,
                        onNoteClick = { note, cvNote ->
                            findNavController().navigate(
                                directions = HomeFragmentDirections.actionHomeFragmentToNoteFragment(
                                    noteId = note.noteId,
                                    noteTitle = note.title,
                                    noteContent = note.content
                                ),
                                navigatorExtras = FragmentNavigatorExtras(cvNote to cvNote.transitionName)
                            )
                        },
                        onWarningClick = {
                            // TODO: Show biometric prompt
                        }
                    )

                    binding.rvSearchResult.adapter = SearchResultRecyclerViewAdapter(
                        results = uiState.searchResults,
                        onResultClick = { noteTitle ->
                            // TODO: Get notes from database by noteTitle
                        }
                    )
                }
            }
        }
    }

    private fun setupNoteRecyclerView() {

        val swapNotesCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ): Int {

                val isNote = viewHolder.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE
                val dragFlags = if (isNote) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                destination: RecyclerView.ViewHolder
            ): Boolean {

                val adapter = recyclerView.adapter as NoteRecyclerViewAdapter
                val isSourceNote = source.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE
                val isDestinationNote =
                    destination.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE

                return if (isSourceNote && isDestinationNote) {
                    homeViewModel.swapNotes(source.adapterPosition, destination.adapterPosition)
                    adapter.notifyItemMoved(source.adapterPosition, destination.adapterPosition)
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

    private fun setupNoteSearchView() {

        // On search view visibility change
        binding.svNote.addTransitionListener { searchView, previousState, newState ->

            if (newState == SearchView.TransitionState.SHOWING) {
                binding.fabNote.hide()
            } else if (newState == SearchView.TransitionState.HIDDEN) {
                binding.sbNote.setText(binding.svNote.text)
                binding.fabNote.show()

                if (binding.svNote.text.isBlank()) {
                    homeViewModel.getNotes()
                }
            }
        }

        // On query text change
        binding.svNote.editText.addTextChangedListener { query ->
            homeViewModel.getSearchResults(query.toString())
        }

        // On query submit
        binding.svNote.editText.setOnEditorActionListener { view, actionId, keyEvent ->

            val query = binding.svNote.text.toString()
            if (query.isNotBlank()) {
                homeViewModel.getNotes(query)
            }
            binding.svNote.hide()
            true
        }
    }
}