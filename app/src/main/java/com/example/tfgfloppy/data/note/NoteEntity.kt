package com.example.tfgfloppy.data.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey
    val id: Int,
    var content: String
)