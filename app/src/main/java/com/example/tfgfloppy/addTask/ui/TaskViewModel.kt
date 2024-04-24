package com.example.tfgfloppy.addTask.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import javax.inject.Inject

class TaskViewModel @Inject constructor(): ViewModel() {

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean>
        get() = _showDialog

    private val _task = mutableStateListOf<TaskModel>()
    val task: List<TaskModel>
        get() = _task

    fun dialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(it: String) {
        _showDialog.value = false
        _task.add(TaskModel(task = it))
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    //Crea otro objeto igual con el valor opuesto.
    fun onCheckBoxSelected(taskModel: TaskModel) {
        val index = _task.indexOf(taskModel)
        _task[index] = _task[index].let {
            it.copy(selected = !it.selected)
        }
        //_task.remove(taskModel)
    }

    fun onItemRemoved(taskModel: TaskModel) {
        val task = _task.find { it.id == taskModel.id }
        _task.remove(task)
    }
}