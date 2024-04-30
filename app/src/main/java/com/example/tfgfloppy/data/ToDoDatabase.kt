package com.example.tfgfloppy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tfgfloppy.data.task.TaskDAO
import com.example.tfgfloppy.data.task.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDAO
}