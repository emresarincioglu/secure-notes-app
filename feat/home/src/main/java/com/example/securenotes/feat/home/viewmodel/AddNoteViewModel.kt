package com.example.securenotes.feat.home.viewmodel

import androidx.lifecycle.ViewModel
import com.example.securenotes.feat.home.model.uistate.AddNoteScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddNoteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddNoteScreenUiState())
    val uiState = _uiState.asStateFlow()
}
