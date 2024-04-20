package com.github.emresarincioglu.securenotes.feat.home.viewmodel

import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.securenotes.feat.home.model.uistate.NoteScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NoteScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun setNoteId(noteId: Int) {
        _uiState.value = _uiState.value.copy(noteId = noteId)
    }

    fun setIsEdited(isEdited: Boolean) {
        if (_uiState.value.isEdited != isEdited) {
            _uiState.value = _uiState.value.copy(isEdited = isEdited)
        }
    }
}