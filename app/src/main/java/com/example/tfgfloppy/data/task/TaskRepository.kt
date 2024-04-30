package com.example.tfgfloppy.data.task

import javax.inject.Inject
import javax.inject.Singleton
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDAO) {

    val tasks: Flow<List<TaskModel>> = taskDao.getAllTask().map { task ->
        task.map { TaskModel(it.id, it.task, it.selected) }
    }

    suspend fun addTask(taskModel: TaskModel) {
        taskDao.addTask(TaskEntity(taskModel.id, taskModel.task, taskModel.selected))
    }

    suspend fun deleteTask(taskModel: TaskModel) {
        taskDao.deleteTask(taskModel.id)
    }
}