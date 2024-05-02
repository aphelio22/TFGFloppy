package com.example.tfgfloppy.addNote.domain

import com.example.tfgfloppy.data.note.NoteRepository
import com.example.tfgfloppy.data.task.TaskRepository
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {

    suspend operator fun invoke(noteModel: NoteModel) {
        noteRepository.updateNote(noteModel)
    }
}