package com.example.tfgfloppy.addTask.domain

import com.example.tfgfloppy.data.task.TaskRepository
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskModel>> {
        return taskRepository.tasks
    }
}