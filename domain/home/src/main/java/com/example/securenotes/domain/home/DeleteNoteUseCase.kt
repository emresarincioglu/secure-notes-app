package com.example.securenotes.domain.home

import com.example.securenotes.core.common.withResult
import com.example.securenotes.data.home.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteId: Int) = withResult {
        noteRepository.deleteNote(noteId)
        true
    }
}
