package com.example.tfgfloppy.ui.model.taskModel

data class TaskModel(
    val id: Long = System.currentTimeMillis(), //Se usa como id para que se autoincremente y nunca haya una id repetida.
    var task: String,
    var selected: Boolean = false
)