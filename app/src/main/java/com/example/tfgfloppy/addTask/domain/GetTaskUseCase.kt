package com.example.tfgfloppy.addTask.domain

import com.example.tfgfloppy.data.task.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(id: Int): Flow<String?> {
        return taskRepository.getTaskContentAsString(id)
    }
}