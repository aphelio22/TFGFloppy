package com.example.tfgfloppy.addTask.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class TaskViewModel @Inject constructor(): ViewModel() {

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean>
        get() = _showDialog

    fun dialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(it: String) {
        _showDialog.value = false
        Log.d("Holi", it.toString())
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }
}