package com.example.securenotes.data.home.local

import android.content.Context
import com.example.securenotes.data.home.CryptoManager
import com.example.securenotes.data.home.local.database.NoteDatabase
import com.example.securenotes.data.home.local.database.entity.NoteEntity
import com.example.securenotes.data.home.model.NoteData
import com.example.securenotes.data.home.readBytes
import com.example.securenotes.data.home.toByteArray
import com.example.securenotes.data.home.toInt
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import java.util.UUID
import javax.inject.Inject

class NoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: NoteDatabase
) {

    companion object {
        private const val NOTE_FILE_EXTENSION = ".snotes"
        private const val NOTE_ORDER_FILE_NAME = "default.snorder"
    }

    suspend fun getNoteSummaries(): List<NoteData> {
        val order = getNoteOrder()
        val notes = mutableListOf<NoteData>()

        if (order == null) {
            return database.noteDao().getAll().map {
                val title = CryptoManager.decrypt(it.title, it.iv)
                val content = CryptoManager.decryptFromFile(
                    context,
                    it.contentFileName,
                    chunkIndex = 0
                )
                NoteData(it.uid, title, content.orEmpty())
            }
        } else {
            for (uid in order) {
                with(database.noteDao().getById(uid)) {
                    val title = CryptoManager.decrypt(title, iv)
                    val content = CryptoManager.decryptFromFile(
                        context,
                        contentFileName,
                        chunkIndex = 0
                    )
                    notes.add(NoteData(uid, title, content.orEmpty()))
                }
            }

            return notes
        }
    }

    suspend fun getNoteContent(noteId: Int, chunkIndex: Int): String? {
        val entity = database.noteDao().getById(noteId)
        return CryptoManager.decryptFromFile(context, entity.contentFileName, chunkIndex)
    }

    suspend fun addNote(note: NoteData) {
        val uuid = UUID.randomUUID().toString()
        val fileName = "$uuid$NOTE_FILE_EXTENSION"

        val (iv, encryptedTitle) = CryptoManager.encrypt(note.title)
        CryptoManager.encryptToFile(context, fileName, note.content.toByteArray())

        val entity = NoteEntity(
            iv = iv,
            title = encryptedTitle,
            contentFileName = fileName,
            lastEditTime = System.currentTimeMillis()
        )
        database.noteDao().add(entity)

        context.openFileOutput(
            NOTE_ORDER_FILE_NAME,
            Context.MODE_PRIVATE or Context.MODE_APPEND
        ).use { outputStream ->
            outputStream.write(database.noteDao().getByFileName(fileName).uid.toByteArray())
        }
    }

    suspend fun updateNote(note: NoteData, updatedChunks: Int) {
        val entity = database.noteDao().getById(note.uid)
        val encryptedTitle = CryptoManager.encrypt(note.title, entity.iv)
        CryptoManager.encryptToFile(
            context,
            entity.contentFileName,
            note.content.toByteArray(),
            updatedChunks
        )
        database.noteDao().update(
            entity.copy(title = encryptedTitle, lastEditTime = System.currentTimeMillis())
        )
    }

    suspend fun deleteNote(noteId: Int) {
        val note = database.noteDao().getById(noteId)
        context.deleteFile(note.contentFileName)
        database.noteDao().delete(note)

        getNoteOrder()?.let { order ->
            val mutableOrder = order.toMutableList()
            mutableOrder.remove(note.uid)
            updateNoteOrder(mutableOrder)
        }
    }

    suspend fun deleteNoteData() {
        context.filesDir.deleteRecursively()
        database.noteDao().deleteAll()
        CryptoManager.deleteKey()
    }

    suspend fun updateNoteOrder(order: List<Int>) {
        context.deleteFile(NOTE_ORDER_FILE_NAME)
        context.openFileOutput(NOTE_ORDER_FILE_NAME, Context.MODE_PRIVATE).use { outputStream ->
            for (uid in order) {
                outputStream.write(uid.toByteArray())
            }
        }
    }

    private suspend fun getNoteOrder(): List<Int>? {
        val order = mutableListOf<Int>()

        try {
            context.openFileInput(NOTE_ORDER_FILE_NAME).use { inputStream ->
                while (true) {
                    val uid = inputStream.readBytes(4)?.toInt() ?: break
                    order.add(uid)
                }
            }
        } catch (ex: FileNotFoundException) {
            return null
        }

        return order
    }
}
