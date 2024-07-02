package com.example.securenotes.feat.home.viewmodel

import androidx.annotation.IntRange
import androidx.lifecycle.ViewModel
import com.example.securenotes.feat.home.model.Note
import com.example.securenotes.feat.home.model.uistate.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
