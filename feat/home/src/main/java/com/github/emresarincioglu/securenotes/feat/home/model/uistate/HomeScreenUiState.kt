package com.github.emresarincioglu.securenotes.feat.home.model.uistate

import com.github.emresarincioglu.securenotes.feat.home.model.Note

data class HomeScreenUiState(
    val notes: List<Note> = listOf(),
    val searchResults: List<String> = listOf()
)