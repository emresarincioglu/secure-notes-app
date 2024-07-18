package com.example.securenotes.feat.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.domain.home.AddNoteUseCase
import com.example.securenotes.domain.home.model.Note
import com.example.securenotes.feat.home.model.uistate.AddNoteScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun addNote() {
        viewModelScope.launch {
            with(uiState.value) {
                addNoteUseCase(Note(title = title, content = content))
            }
        }
    }
}
