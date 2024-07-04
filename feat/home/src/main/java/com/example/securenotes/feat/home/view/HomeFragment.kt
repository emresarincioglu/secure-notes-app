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
import kotlinx.coroutines.launch
import com.example.securenotes.core.navigation.R as coreNavigationR

class HomeFragment : DataBindingFragment<FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { uiState ->

                    if (uiState.isPasswordCreated && !uiState.isAuthenticated) {
                        binding.unbind()
                        findNavController().navigate(
                            coreNavigationR.id.action_global_nav_graph_authentication
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

        binding.rvNote.adapter = NoteRecyclerViewAdapter(
            notes = uiState.notes,
            showWarning = !uiState.isPasswordCreated,
            onNoteClick = { note, cvNote ->

                exitTransition = Hold()
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

        binding.rvSearchResult.adapter = SearchResultRecyclerViewAdapter(
            results = uiState.searchResults,
            onResultClick = { noteTitle ->
                // TODO Get notes from database by noteTitle
            }
        )
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
