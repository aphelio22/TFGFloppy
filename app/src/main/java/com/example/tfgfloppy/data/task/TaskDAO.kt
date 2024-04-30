package com.example.tfgfloppy.data.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDAO {
    @Query("SELECT * FROM task_table")
    fun getAllTask(): Flow<List<TaskEntity>>

    @Insert
    suspend fun addTask(task: TaskEntity)

    @Query("DELETE FROM task_table WHERE id = :id")
    suspend fun deleteTask(id: Int)
}