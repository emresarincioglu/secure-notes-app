package com.github.emresarincioglu.home.model.uistate

import com.github.emresarincioglu.home.model.Note

data class HomeScreenUiState(
    val notes: List<Note> = listOf(),
    val searchResults: List<String> = listOf()
)