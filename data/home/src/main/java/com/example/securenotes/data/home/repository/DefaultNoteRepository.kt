package com.example.securenotes.data.home.repository

import com.example.securenotes.core.common.di.IoDispatcher
import com.example.securenotes.data.home.local.NoteDataSource
import com.example.securenotes.data.home.model.NoteData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultNoteRepository @Inject constructor(
    private val localDataSource: NoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NoteRepository {

    override suspend fun getNoteSummaries(): List<NoteData> = withContext(ioDispatcher) {
        localDataSource.getNoteSummaries()
    }

    override suspend fun getNoteContent(noteId: Int, chunkIndex: Int) = withContext(ioDispatcher) {
        localDataSource.getNoteContent(noteId, chunkIndex)
    }

    override suspend fun addNote(note: NoteData) = withContext(ioDispatcher) {
        localDataSource.addNote(note)
    }

    override suspend fun updateNote(note: NoteData, writeUntilChunk: Int) =
        withContext(ioDispatcher) {
            localDataSource.updateNote(note, writeUntilChunk)
        }

    override suspend fun deleteNote(noteId: Int) = withContext(ioDispatcher) {
        localDataSource.deleteNote(noteId)
    }

    override suspend fun deleteData() = withContext(ioDispatcher) {
        localDataSource.deleteNoteData()
    }

    override suspend fun updateNoteOrder(order: List<Int>) = withContext(ioDispatcher) {
        localDataSource.updateNoteOrder(order)
    }
}
