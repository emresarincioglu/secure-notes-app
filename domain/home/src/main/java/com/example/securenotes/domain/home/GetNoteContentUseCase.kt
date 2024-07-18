package com.example.securenotes.domain.home

import com.example.securenotes.data.home.repository.NoteRepository
import javax.inject.Inject

class GetNoteContentUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteId: Int, chunkIndex: Int) =
        noteRepository.getNoteContent(noteId, chunkIndex)
}
