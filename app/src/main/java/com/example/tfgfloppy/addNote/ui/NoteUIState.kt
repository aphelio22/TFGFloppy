package com.example.tfgfloppy.addNote.ui

import com.example.tfgfloppy.ui.model.noteModel.NoteModel

sealed interface NoteUIState {
    data object Loading: NoteUIState
    data class Error(val throwable: Throwable): NoteUIState
    data class Success(val note: List<NoteModel>): NoteUIState
}
