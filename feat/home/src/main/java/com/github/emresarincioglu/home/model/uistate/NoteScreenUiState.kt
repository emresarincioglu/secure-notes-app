package com.github.emresarincioglu.home.model.uistate

data class NoteScreenUiState(
    val noteId: Int = 0,
    var title: String = "",
    var content: String = "",
    val isEdited: Boolean = false
)
