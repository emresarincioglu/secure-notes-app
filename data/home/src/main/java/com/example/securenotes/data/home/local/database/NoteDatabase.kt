package com.example.securenotes.data.home.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.securenotes.data.home.local.database.dao.NoteDao
import com.example.securenotes.data.home.local.database.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
