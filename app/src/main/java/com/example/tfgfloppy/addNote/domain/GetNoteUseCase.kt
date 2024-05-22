package com.example.tfgfloppy.addNote.domain

import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    operator fun invoke(): Flow<List<NoteModel>> {
        return noteRepository.notes
    }
}