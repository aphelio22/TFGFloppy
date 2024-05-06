package com.example.tfgfloppy.addTask.domain

import com.example.tfgfloppy.data.task.TaskRepository
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(taskModel: TaskModel) {
        taskRepository.deleteTask(taskModel)
    }
}