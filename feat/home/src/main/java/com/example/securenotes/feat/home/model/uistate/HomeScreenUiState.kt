package com.example.securenotes.feat.home.model.uistate

import com.example.securenotes.feat.home.model.Note

data class HomeScreenUiState(
    val isAuthenticated: Boolean = false,
    val isPasswordCreated: Boolean = false,
    val notes: List<Note> = listOf(),
    val searchResults: List<String> = listOf()
)
