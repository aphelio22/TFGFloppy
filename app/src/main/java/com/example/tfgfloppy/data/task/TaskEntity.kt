package com.example.tfgfloppy.data.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    var task: String,
    var selected: Boolean = false
)