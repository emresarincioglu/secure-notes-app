package com.example.securenotes.data.home.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["iv"], unique = true),
        Index(value = ["contentFileName"], unique = true)
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val uid: Int = 0,
    val title: String,
    val contentFileName: String,
    val lastEditTime: Long,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val iv: ByteArray
)
