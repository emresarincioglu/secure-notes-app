package com.example.securenotes.domain.home.model

import com.example.securenotes.data.home.model.NoteData

data class Note(
    val noteId: Int = 0,
    val title: String = "",
    val content: String = ""
) {
    fun toNoteData() = NoteData(noteId, title, content)
}

fun NoteData.toNote() = Note(uid, title, content)
