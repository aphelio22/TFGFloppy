package com.example.navegacionconbotonflotante.composable.screens.taskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.tfgfloppy.addTask.ui.TaskViewModel


@Composable
fun MyTaskScreen(navController: NavController, taskViewModel: TaskViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        addTaskDialog(show = true) {
            
        }
        FabDialog(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 20.dp))
    }
}

@Composable
private fun FabDialog(align: Modifier) {
    FloatingActionButton(
        onClick = {
            //Mostrar diálogo.
        },
        modifier = align
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir tarea")
    }
}

@Composable
private fun addTaskDialog(show: Boolean, onDismiss:() -> Unit) {
    var myTask by remember {
        mutableStateOf("")
    }
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                Text(text = "Añade tu tarea", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = myTask, onValueChange = {myTask = it})
                Spacer(modifier = Modifier.size(16.dp))
                Button(onClick = {
                    //Mandar tarea
                }) {
                    Text(text = "Añadir tarea")
                }
            }
        }
    }
}