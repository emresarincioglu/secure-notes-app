package com.example.securenotes.domain.home

import com.example.securenotes.data.home.model.NoteData
import com.example.securenotes.data.home.repository.NoteRepository
import com.example.securenotes.domain.home.model.toNote
import javax.inject.Inject

class GetNoteSummariesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke() = noteRepository.getNoteSummaries().map(NoteData::toNote)
}
