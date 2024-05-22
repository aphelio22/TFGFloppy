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
        taskDao.addTask(taskModel.toEntity())
    }

    suspend fun deleteTask(taskModel: TaskModel) {
        taskDao.deleteTask(taskModel.toEntity())
    }

    suspend fun check(taskModel: TaskModel) {
        taskDao.checkTask(taskModel.toEntity())
    }

    suspend fun updateContent(taskModel: TaskModel) {
        taskDao.updateContent(taskModel.toEntity())
    }

    suspend fun getTaskContentAsString(taskId: Int): Flow<String?> {
        return taskDao.getTaskContent(taskId)
    }


}

fun TaskModel.toEntity(): TaskEntity {
    return TaskEntity(this.id, this.task, this.selected)
}