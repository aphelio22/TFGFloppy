package com.example.tfgfloppy.data.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDAO {
    @Query("SELECT * FROM task_table")
    fun getAllTask(): Flow<List<TaskEntity>>

    @Insert
    suspend fun addTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Update
    suspend fun checkTask(task: TaskEntity)

    @Update
    suspend fun updateContent(task: TaskEntity)

    @Query("SELECT task FROM task_table WHERE id = :taskId")
    fun getTaskContent(taskId: Int): Flow<String?>
}