package com.example.securenotes.feat.home.model.uistate

import com.example.securenotes.domain.home.model.Note

data class HomeScreenUiState(
    val notes: List<Note> = listOf(),
    val isAuthenticated: Boolean? = null,
    val searchResults: List<String> = listOf()
)
