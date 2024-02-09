package com.github.emresarincioglu.home.viewmodel

import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.home.model.uistate.AddNoteScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddNoteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteScreenUiState())
    val uiState = _uiState.asStateFlow()
}