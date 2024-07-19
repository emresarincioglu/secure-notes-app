package com.example.securenotes.feat.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.common.Result
import com.example.securenotes.domain.home.DeleteNoteUseCase
import com.example.securenotes.domain.home.GetNoteContentUseCase
import com.example.securenotes.domain.home.UpdateNoteUseCase
import com.example.securenotes.domain.home.model.Note
import com.example.securenotes.feat.home.model.uistate.NoteScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteContentUseCase: GetNoteContentUseCase
) : ViewModel() {

    private var contentNumber = 1

    private val _getNoteContentResultStream = MutableStateFlow<Result<String?>?>(null)
    val getNoteContentResultStream = _getNoteContentResultStream.asStateFlow()

    private val _deleteNoteResultStream = MutableSharedFlow<Result<Boolean>>()
    val deleteNoteResultStream = _deleteNoteResultStream.asSharedFlow()

    private val _updateNoteResultStream = MutableSharedFlow<Result<Boolean>>()
    val updateNoteResultStream = _updateNoteResultStream.asSharedFlow()

    private val _uiState = MutableStateFlow(NoteScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun setNoteId(noteId: Int) {
        _uiState.value = uiState.value.copy(noteId = noteId)
    }

    fun setIsEdited(isEdited: Boolean) {
        if (_uiState.value.isEdited != isEdited) {
            _uiState.value = uiState.value.copy(isEdited = isEdited)
        }
    }

    fun getMoreNoteContent() {
        _getNoteContentResultStream.value = Result.Loading
        viewModelScope.launch {
            val newContent = getNoteContentUseCase(uiState.value.noteId, contentNumber++)
            if (newContent == null) {
                _getNoteContentResultStream.value = Result.Success(null)
            } else {
                _getNoteContentResultStream.value = Result.Success(newContent)
            }
        }
    }

    fun updateNote() {
        viewModelScope.launch {
            _updateNoteResultStream.emit(Result.Loading)
            with(uiState.value) {
                _updateNoteResultStream.emit(
                    updateNoteUseCase(Note(noteId, title, content), contentNumber)
                )
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            _deleteNoteResultStream.emit(Result.Loading)
            _deleteNoteResultStream.emit(deleteNoteUseCase(uiState.value.noteId))
        }
    }
}
