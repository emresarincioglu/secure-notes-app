package com.example.securenotes.data.home.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.securenotes.data.home.local.database.entity.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE rowid = :id LIMIT 1")
    suspend fun getById(id: Int): NoteEntity

    @Query("SELECT * FROM notes WHERE contentFileName = :fileName")
    suspend fun getByFileName(fileName: String): NoteEntity

    @Query("SELECT * FROM notes ORDER BY rowid")
    suspend fun getAll(): List<NoteEntity>

    @Insert
    suspend fun add(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}
