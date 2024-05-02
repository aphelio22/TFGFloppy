package com.example.tfgfloppy.ui.model.noteModel

data class NoteModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    var content: String
)