package com.example.tfgfloppy.addNote.domain

import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import javax.inject.Inject

class DeleteAllNotesUseCase@Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke() {
        noteRepository.deleteAllNotes()
    }
}