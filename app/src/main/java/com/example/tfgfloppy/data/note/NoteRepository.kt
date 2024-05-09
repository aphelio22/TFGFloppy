package com.example.tfgfloppy.data.note

import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val noteDAO: NoteDAO) {

    val notes: Flow<List<NoteModel>> = noteDAO.getAllNotes().map { note ->
        note.map { NoteModel(it.id, it.content)}
    }

    suspend fun addNote(note: NoteModel) {
        noteDAO.addNote(note.toEntity())
    }

    suspend fun deleteNote(note: NoteModel) {
        noteDAO.deleteNote(note.toEntity())
    }

    suspend fun updateNote(note: NoteModel) {
        noteDAO.updateNote(note.toEntity())
    }
}

fun NoteModel.toEntity(): NoteEntity {
    return NoteEntity(this.id, this.content)
}