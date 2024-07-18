package com.example.securenotes.data.home.repository

import com.example.securenotes.data.home.model.NoteData

interface NoteRepository {

    suspend fun getNoteSummaries(): List<NoteData>

    suspend fun getNoteContent(noteId: Int, chunkIndex: Int): String?

    suspend fun addNote(note: NoteData)

    suspend fun updateNote(note: NoteData, writeUntilChunk: Int)

    suspend fun deleteNote(noteId: Int)

    suspend fun deleteData()

    suspend fun updateNoteOrder(order: List<Int>)
}
