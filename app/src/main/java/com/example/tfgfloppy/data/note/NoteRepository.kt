package com.example.tfgfloppy.data.note

import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val noteDAO: NoteDAO, private val firestore: FirebaseFirestore) {

    val notes: Flow<List<NoteModel>> = noteDAO.getAllNotes().map { note ->
        note.map { NoteModel(it.id, it.content)}
    }

    suspend fun addNote(note: NoteModel) {
        noteDAO.addNote(note.toEntity())
    }

    suspend fun insertNotes(notesList: List<NoteModel>) {
        noteDAO.insertNotes(notesList.toEntityList())
    }

    suspend fun deleteNote(note: NoteModel) {
        noteDAO.deleteNote(note.toEntity())
    }

    suspend fun updateNote(note: NoteModel) {
        noteDAO.updateNote(note.toEntity())
    }

    suspend fun deleteAllNotes() {
        noteDAO.deleteAllNotes()
    }

    suspend fun getNotesFromFirestore(): List<NoteModel> {
        val notes = mutableListOf<NoteModel>()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val querySnapshot = firestore.collection("user").document(userId)
                .collection("note").get().await()

            for (document in querySnapshot.documents) {
                val content = document.getString("content")
                content?.let { notes.add(NoteModel(content = it)) }
            }
        }
        return notes
    }
}

fun NoteModel.toEntity(): NoteEntity {
    return NoteEntity(this.id, this.content)
}

fun List<NoteModel>.toEntityList(): List<NoteEntity> {
    return this.map { noteModel ->
        NoteEntity(
            id = noteModel.id,
            content = noteModel.content
        )
    }
}