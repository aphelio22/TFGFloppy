package com.example.tfgfloppy.data.note

import com.example.tfgfloppy.data.task.TaskEntity
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import kotlin.concurrent.thread

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