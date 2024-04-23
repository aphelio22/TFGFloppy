package com.example.navegacionconbotonflotante.composable.screens.taskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.tfgfloppy.addTask.ui.TaskViewModel


@Composable
fun MyTaskScreen(navController: NavController, taskViewModel: TaskViewModel) {

    val showDialog: Boolean by taskViewModel.showDialog.observeAsState(false)

    Box(modifier = Modifier.fillMaxSize()) {
        AddTaskDialog(showDialog, onDismiss = {taskViewModel.dialogClose()}, onTaskAdded = {taskViewModel.onTaskCreated(it)})
        FabDialog(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 20.dp),
            taskViewModel)
        TaskList(taskViewModel)
    }
}

@Composable
fun TaskList(taskViewModel: TaskViewModel) {
    LazyColumn {

    }
}

@Composable
private fun FabDialog(align: Modifier, taskViewModel: TaskViewModel) {
    FloatingActionButton(
        onClick = {
            taskViewModel.onShowDialogClick()
        },
        modifier = align
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir tarea")
    }
}

@Composable
private fun AddTaskDialog(show: Boolean, onDismiss:() -> Unit, onTaskAdded:(String) -> Unit) {
    var myTask by remember {
        mutableStateOf("")
    }
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(value = myTask, onValueChange = {myTask = it}, label = { Text(text = "Añadir nota:")})
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    onTaskAdded(myTask)
                }) {
                    Text(text = "Añadir tarea")
                }
            }
        }
    }
}