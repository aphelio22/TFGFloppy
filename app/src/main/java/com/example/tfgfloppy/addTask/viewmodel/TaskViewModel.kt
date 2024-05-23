package com.example.tfgfloppy.addTask.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addTask.domain.AddTaskUseCase
import com.example.tfgfloppy.addTask.domain.CheckTaskUseCase
import com.example.tfgfloppy.addTask.domain.DeleteTaskUseCase
import com.example.tfgfloppy.addTask.domain.GetTaskUseCase
import com.example.tfgfloppy.addTask.domain.GetTasksUseCase
import com.example.tfgfloppy.addTask.domain.UpdateTaskContentUseCase
import com.example.tfgfloppy.addTask.ui.TaskUIState
import com.example.tfgfloppy.addTask.ui.TaskUIState.Success
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val checkTaskUseCase: CheckTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskContentUseCase,
    getTasksUseCase: GetTasksUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : ViewModel() {

    val uiState: StateFlow<TaskUIState> = getTasksUseCase().map(::Success)
        .catch { TaskUIState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskUIState.Loading)

    private val _showAddDialog = MutableLiveData<Boolean>()
    val showAddDialog: LiveData<Boolean>
        get() = _showAddDialog


    private val _taskContent = MutableLiveData<String>()
    val taskContent: LiveData<String>
        get() = _taskContent

    fun dialogClose() {
        _showAddDialog.value = false
    }

    fun onTaskCreated(it: String) {
        _showAddDialog.value = false

        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = it))
        }
    }

    fun onShowDialogToAddTask() {
        _showAddDialog.value = true
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        viewModelScope.launch {
            checkTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }
    }

    fun onTaskRemoved(taskModel: TaskModel) {
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
    }

    fun onTaskUpdated(taskModel: TaskModel, content: String) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(task = content))
        }
    }

    fun fetchTaskContent(taskId: Int) {
        viewModelScope.launch {
            getTaskUseCase.invoke(taskId).collect { taskContent ->
                _taskContent.postValue(taskContent ?: "")
            }
        }
    }
}