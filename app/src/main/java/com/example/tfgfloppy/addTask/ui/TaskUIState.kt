package com.example.tfgfloppy.addTask.ui

import com.example.tfgfloppy.data.task.TaskEntity
import com.example.tfgfloppy.ui.model.taskModel.TaskModel

sealed interface TaskUIState {
    object Loading: TaskUIState
    data class Error(val throwable: Throwable): TaskUIState
    data class Success(val task: List<TaskModel>): TaskUIState
}