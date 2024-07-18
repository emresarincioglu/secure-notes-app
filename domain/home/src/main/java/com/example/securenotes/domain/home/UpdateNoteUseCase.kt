package com.example.securenotes.domain.home

import com.example.securenotes.core.common.withResult
import com.example.securenotes.data.home.repository.NoteRepository
import com.example.securenotes.domain.home.model.Note
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: Note, loadedChunks: Int) = withResult {
        noteRepository.updateNote(note.toNoteData(), loadedChunks - 1)
        true
    }
}
