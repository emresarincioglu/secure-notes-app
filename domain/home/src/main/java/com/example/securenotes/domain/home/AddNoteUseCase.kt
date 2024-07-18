package com.example.securenotes.domain.home

import com.example.securenotes.data.home.repository.NoteRepository
import com.example.securenotes.domain.home.model.Note
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: Note) = noteRepository.addNote(note.toNoteData())
}
