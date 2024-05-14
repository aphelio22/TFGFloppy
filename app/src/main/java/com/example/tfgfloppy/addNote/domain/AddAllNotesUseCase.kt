package com.example.tfgfloppy.addNote.domain

import com.example.tfgfloppy.data.note.NoteEntity
import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import javax.inject.Inject

class AddAllNotesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(notesList: List<NoteModel>) {
        noteRepository.insertNotes(notesList)
    }
}