package com.example.securenotes.feat.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.core.ui.DataBindingFragment
import com.example.securenotes.feat.home.NoteRecyclerViewAdapter
import com.example.securenotes.feat.home.R
import com.example.securenotes.feat.home.SearchResultRecyclerViewAdapter
import com.example.securenotes.feat.home.databinding.FragmentHomeBinding
import com.example.securenotes.feat.home.model.uistate.HomeScreenUiState
import com.example.securenotes.feat.home.viewmodel.HomeViewModel
import com.google.android.material.search.SearchView
import com.google.android.material.transition.Hold
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.securenotes.core.navigation.R as coreNavigationR

@AndroidEntryPoint
class HomeFragment : DataBindingFragment<FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflateBinding(R.layout.fragment_home, inflater, container, false)
        binding.viewModel = homeViewModel

        // TODO: Show logo if there are no notes
        // FIXME: Search view back button not closing search view
        // TODO: Show note last edit time
        setupViews()
        observeUiState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun setupViews() {
        homeViewModel.loadNotes()

        setupNoteRecyclerView()
        setupNoteSearchView()
        binding.sbNote.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.menu_item_settings) {
                exitTransition = null
                findNavController().navigate(R.id.action_homeFragment_to_settingsGraph)
                true
            } else {
                false
            }
        }

        binding.fabNote.setOnClickListener {
            exitTransition = Hold()
            findNavController().navigate(
                R.id.action_homeFragment_to_addNoteFragment,
                args = null,
                navOptions = null,
                FragmentNavigatorExtras(binding.fabNote to binding.fabNote.transitionName)
            )
        }
    }

    private fun observeUiState() {
        val isPasswordCreated = requireArguments().getBoolean("is_password_created")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { uiState ->

                    if (isPasswordCreated && uiState.isAuthenticated == false) {
                        findNavController().navigate(
                            coreNavigationR.id.action_global_to_authenticationGraph
                        )
                    }

                    setupRecyclerViewAdapters(uiState)
                }
            }
        }
    }

    private fun setupNoteRecyclerView() {

        val swapNotesCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val isNote = viewHolder.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE
                val dragFlags = if (isNote) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0

                val movementFlags = if (binding.sbNote.text.isBlank()) {
                    makeMovementFlags(dragFlags, 0)
                } else {
                    makeMovementFlags(0, 0)
                }

                return movementFlags
            }

            override fun onMove(
                recyclerView: RecyclerView,
                destination: RecyclerView.ViewHolder,
                source: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter as NoteRecyclerViewAdapter
                val isSourceNote = source.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE
                val isDestinationNote =
                    destination.itemViewType == NoteRecyclerViewAdapter.ITEM_TYPE_NOTE

                return if (isSourceNote && isDestinationNote) {
                    val isPasswordCreated = requireArguments().getBoolean("is_password_created")
                    val sub = if (isPasswordCreated) 1 else 2

                    val fromIndex = source.adapterPosition - sub
                    val toIndex = destination.adapterPosition - sub

                    homeViewModel.swapNotes(fromIndex, toIndex)
                    adapter.notifyItemMoved(source.adapterPosition, destination.adapterPosition)
                    true
                } else {
                    false
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.startAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.opaque_to_translucent).apply {
                            fillAfter = true
                        }
                    )
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                homeViewModel.updateNoteOrder()
                viewHolder.itemView.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.translucent_to_opaque).apply {
                        fillAfter = true
                    }
                )
            }
        }

        ItemTouchHelper(swapNotesCallback).attachToRecyclerView(binding.rvNote)
    }

    private fun setupRecyclerViewAdapters(uiState: HomeScreenUiState) {
        val isPasswordCreated = requireArguments().getBoolean("is_password_created")

        if (binding.rvNote.adapter == null) {
            binding.rvNote.adapter = NoteRecyclerViewAdapter(
                notes = uiState.notes,
                showWarning = !isPasswordCreated,
                onNoteClick = { note, cvNote ->
                    exitTransition = Hold()
                    binding.fabNote.visibility = View.GONE

                    findNavController().navigate(
                        R.id.action_homeFragment_to_noteFragment,
                        args = Bundle().apply {
                            putInt("note_id", note.noteId)
                            putString("note_title", note.title)
                            putString("note_content", note.content)
                        },
                        navOptions = null,
                        FragmentNavigatorExtras(cvNote to cvNote.transitionName)
                    )
                },
                onWarningClick = {
                    exitTransition = null
                    findNavController().navigate(
                        R.id.action_homeFragment_to_settingsGraph,
                        args = Bundle().apply {
                            putBoolean("show_password_dialog", true)
                        }
                    )
                }
            )
        } else {
            val adapter = binding.rvNote.adapter as NoteRecyclerViewAdapter
            adapter.updateData(uiState.notes)
        }

        if (binding.rvSearchResult.adapter == null) {
            binding.rvSearchResult.adapter = SearchResultRecyclerViewAdapter(
                results = uiState.searchResults,
                onResultClick = { noteTitle ->
                    binding.svNote.setText(noteTitle)
                    homeViewModel.getNotesBySearch()
                    binding.svNote.hide()
                }
            )
        } else {
            val adapter = binding.rvSearchResult.adapter as SearchResultRecyclerViewAdapter
            adapter.updateData(uiState.searchResults)
        }
    }

    private fun setupNoteSearchView() {
        // TODO Add previous search results with different icon

        // On search view visibility change
        binding.svNote.addTransitionListener { searchView, previousState, newState ->
            when (newState) {
                SearchView.TransitionState.SHOWING -> binding.fabNote.hide()

                SearchView.TransitionState.HIDING -> {
                    if (searchView.text.isBlank()) {
                        homeViewModel.getNotesBySearch()
                    }
                }

                SearchView.TransitionState.HIDDEN -> {
                    binding.sbNote.setText(searchView.text)
                    binding.fabNote.show()
                }

                else -> Unit
            }
        }

        // On query text change
        binding.svNote.editText.addTextChangedListener { query ->
            homeViewModel.getSearchResults(query?.toString() ?: "")
        }

        // On query submit
        binding.svNote.editText.setOnEditorActionListener { view, actionId, keyEvent ->
            val query = binding.svNote.text.toString()
            if (query.isNotBlank()) {
                homeViewModel.getNotesBySearch()
            }
            binding.svNote.hide()
            true
        }
    }
}
