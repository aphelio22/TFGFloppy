package com.example.tfgfloppy.addNote.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addNote.domain.AddNoteUseCase
import com.example.tfgfloppy.addNote.domain.DeleteAllNotesUseCase
import com.example.tfgfloppy.addNote.domain.DeleteNoteUseCase
import com.example.tfgfloppy.addNote.domain.GetNoteUseCase
import com.example.tfgfloppy.addNote.domain.UpdateNoteUseCase
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase, private val deleteNoteUseCase: DeleteNoteUseCase, private val updateNoteUseCase: UpdateNoteUseCase, private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore, private val deleteAllNotesUseCase: DeleteAllNotesUseCase, getNoteUseCase: GetNoteUseCase): ViewModel() {

    val uiState: StateFlow<NoteUIState> = getNoteUseCase().map (NoteUIState::Success)
        .catch { NoteUIState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteUIState.Loading)

    private val _showDialogToDeleteNotes = MutableLiveData<Boolean>()
    val showDialogToDeleteNotes: LiveData<Boolean>
        get() = _showDialogToDeleteNotes

    fun onShowDialogToDeleteNotes() {
        _showDialogToDeleteNotes.value = true
    }

    fun dialogClose() {
        _showDialogToDeleteNotes.value = false
    }

    fun addNote(note: String) {
        viewModelScope.launch {
            addNoteUseCase(NoteModel(content = note))
        }
    }

    fun addNoteFromFireStore(note: NoteModel) {
        viewModelScope.launch {
            addNoteUseCase(NoteModel(content = note.content))
        }
    }

    fun updateNote(noteModel: NoteModel, content: String) {
        viewModelScope.launch {
            updateNoteUseCase(noteModel.copy(content = content))
        }
    }

    fun deleteNote(noteModel: NoteModel) {
        viewModelScope.launch {
            deleteNoteUseCase(noteModel)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            deleteAllNotesUseCase.invoke()
        }
    }

    fun getNotesFromFirestore() {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            // Referencia a la colección de notas del usuario actual
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")

            // Obtener todas las notas existentes en la colección
            notesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    val notesList = mutableListOf<NoteModel>()
                    for (document in documents) {
                        val id = document.getLong("id")?.toInt()
                        val content = document.getString("content")
                        if (id != null && content != null) {
                            val note = NoteModel(id = id.toInt(), content = content)
                            notesList.add(note)
                        }
                    }
                    deleteAllNotes()
                    // Agregar las notas a Room
                    for (note in notesList) {
                        if (note.content != "") {
                            addNoteFromFireStore(note)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error getting documents: $e")
                }
        }
    }
}