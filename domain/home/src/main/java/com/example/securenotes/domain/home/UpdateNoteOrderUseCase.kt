package com.example.securenotes.domain.home

import com.example.securenotes.data.home.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteOrderUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(ids: List<Int>) = noteRepository.updateNoteOrder(ids)
}
