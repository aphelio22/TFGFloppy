package com.example.tfgfloppy.addNote.domain

import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(noteModel: NoteModel) {
        noteRepository.addNote(noteModel)
    }
}