package com.example.tfgfloppy.ui.model.taskModel

data class TaskModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    var task: String,
    var selected: Boolean = false
)