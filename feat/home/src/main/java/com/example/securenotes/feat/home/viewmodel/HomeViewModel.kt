package com.example.securenotes.feat.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.domain.authentication.GetIsAuthenticatedStreamUseCase
import com.example.securenotes.domain.home.GetNoteSummariesUseCase
import com.example.securenotes.domain.home.UpdateNoteOrderUseCase
import com.example.securenotes.domain.home.model.Note
import com.example.securenotes.feat.home.model.uistate.HomeScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val updateNoteOrderUseCase: UpdateNoteOrderUseCase,
    private val getNoteSummariesUseCase: GetNoteSummariesUseCase,
    getIsAuthenticatedStreamUseCase: GetIsAuthenticatedStreamUseCase
) : ViewModel() {

    companion object {
        private const val FLOW_SUBSCRIPTION_TIMEOUT = 500L
    }

    private lateinit var loadedNotes: List<Note>
    private lateinit var filteredNoteIndices: List<Int>

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.combine(getIsAuthenticatedStreamUseCase()) { uiState, isAuthenticated ->
        uiState.copy(isAuthenticated = isAuthenticated)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(FLOW_SUBSCRIPTION_TIMEOUT),
        _uiState.value
    )

    fun loadNotes() {
        viewModelScope.launch {
            loadedNotes = getNoteSummariesUseCase()
            _uiState.value = uiState.value.copy(notes = loadedNotes)
        }
    }

    fun getNotesBySearch() {
        val filteredNotes = mutableListOf<Note>()
        for (index in filteredNoteIndices) {
            filteredNotes.add(loadedNotes[index])
        }

        _uiState.value = uiState.value.copy(
            notes = if (filteredNoteIndices.isEmpty()) loadedNotes else filteredNotes
        )
    }

    fun getSearchResults(title: String) {
        if (title.isBlank()) {
            filteredNoteIndices = emptyList()
            _uiState.value = uiState.value.copy(searchResults = emptyList())
            return
        }

        val filteredNotes = loadedNotes.withIndex().filter {
            it.value.title.contains(title, ignoreCase = true)
        }

        filteredNoteIndices = filteredNotes.map { it.index }
        _uiState.value = uiState.value.copy(searchResults = filteredNotes.map { it.value.title })
    }

    fun swapNotes(from: Int, to: Int) {
        loadedNotes = loadedNotes.mapIndexed { index, note ->
            when (index) {
                from -> loadedNotes[to]
                to -> loadedNotes[from]
                else -> note
            }
        }
    }

    fun updateNoteOrder() {
        viewModelScope.launch {
            updateNoteOrderUseCase(loadedNotes.map { it.noteId })
        }
    }
}
