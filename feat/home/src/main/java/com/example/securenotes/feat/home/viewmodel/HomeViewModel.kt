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

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun swapNotes(@IntRange(from = 0) from: Int, @IntRange(from = 0) to: Int) {

        if (from != to) {
            // TODO: Swap notes in database
        }
    }

    fun getNotes() {
        // TODO: Get all notes
    }

    fun getNotes(query: String) {
        // TODO: Get notes by query
    }

    fun getSearchResults(query: String) {

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        } else {
            // TODO: Get search results by query
        }
    }
}
