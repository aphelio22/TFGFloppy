package com.example.tfgfloppy.addNote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addNote.domain.AddNoteUseCase
import com.example.tfgfloppy.addNote.domain.DeleteNoteUseCase
import com.example.tfgfloppy.addNote.domain.GetNoteUseCase
import com.example.tfgfloppy.addNote.domain.UpdateNoteUseCase
import com.example.tfgfloppy.addTask.ui.TaskUIState
import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase, private val deleteNoteUseCase: DeleteNoteUseCase, private val updateNoteUseCase: UpdateNoteUseCase, getNoteUseCase: GetNoteUseCase): ViewModel() {

    val uiState: StateFlow<NoteUIState> = getNoteUseCase().map (NoteUIState::Success)
        .catch { NoteUIState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteUIState.Loading)

    private val _showAddDialog = MutableLiveData<Boolean>()
    val showAddDialog: LiveData<Boolean>
        get() = _showAddDialog

    fun addNote(note: String) {
        viewModelScope.launch {
            addNoteUseCase(NoteModel(content = note))
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

    fun onShowDialogToAddTask() {
        _showAddDialog.value = true
    }

    fun dialogClose() {
        _showAddDialog.value = false
        //_showEditDialog.value = false
    }
}