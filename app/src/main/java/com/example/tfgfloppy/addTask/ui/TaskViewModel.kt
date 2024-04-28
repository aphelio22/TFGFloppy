package com.example.tfgfloppy.addTask.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import javax.inject.Inject

class TaskViewModel @Inject constructor(): ViewModel() {

    private val _showAddDialog = MutableLiveData<Boolean>()
    val showAddDialog: LiveData<Boolean>
        get() = _showAddDialog


    private val _showEditDialog = MutableLiveData<Boolean>()
    val showEditDialog: LiveData<Boolean>
        get() = _showEditDialog


    private val _task = mutableStateListOf<TaskModel>()
    val task: List<TaskModel>
        get() = _task

    private val _selectedTask = mutableStateOf<TaskModel?>(null)
    val selectedTask: TaskModel?
        get() = _selectedTask.value

    /*
    private val _taskToEdit = MutableLiveData<TaskModel?>(null)
    val taskToEdit: LiveData<TaskModel?>
        get() = _taskToEdit
    */

    fun dialogClose() {
        _showAddDialog.value = false
        //_showEditDialog.value = false
    }

    fun onTaskCreated(it: String) {
        _showAddDialog.value = false
        _task.add(TaskModel(task = it))
    }

    fun onShowDialogToAddTask() {
        _showAddDialog.value = true
    }

    fun onShowDialogToEditTask(taskToEdit: TaskModel) {
        _showEditDialog.value = true
    }

    fun selectTask(taskModel: TaskModel) {
        _selectedTask.value = taskModel
    }

    fun clearSelectedTask() {
        _selectedTask.value = null
    }



    //Crea otro objeto igual con el valor opuesto.
    fun onCheckBoxSelected(taskModel: TaskModel) {
        val index = _task.indexOf(taskModel)
        _task[index] = _task[index].let {
            it.copy(selected = !it.selected)
        }
    }

    fun onTextEdited(taskModel: TaskModel, task: String) {
        val index = _task.indexOf(taskModel)
        _task[index] = _task[index].copy(task = task)
    }

    fun onTaskRemoved(taskModel: TaskModel) {
        val task = _task.find { it.id == taskModel.id }
        _task.remove(task)
    }

    /*
    fun onTaskEdited(newText: String) {
        val index = 0
        if (index in task.indices) {
            val currentTask = task[index]
            val updatedTask = currentTask.copy(task = newText)
            _task[index] = updatedTask
        }
    }
     */
}