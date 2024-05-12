package com.example.tfgfloppy.addTask.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.tfgfloppy.R
import com.example.tfgfloppy.firebase.viewmodel.AuthViewModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MyTaskScreen(taskViewModel: TaskViewModel, authViewModel: AuthViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val fontFamilyRobotoRegular = FontFamily(Font(R.font.roboto_regular))
    val showAddDialog: Boolean by taskViewModel.showAddDialog.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by produceState<TaskUIState> (
        initialValue = TaskUIState.Loading,
        key1 = lifecycle,
        key2 = taskViewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            taskViewModel.uiState.collect{ value = it }
        }
    }

    when(uiState) {
        is TaskUIState.Error -> {
            TODO()
        }
        TaskUIState.Loading -> {
            CircularProgressIndicator()
        }
        is TaskUIState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AddTaskDialog(
                    showAddDialog,
                    onDismiss = { taskViewModel.dialogClose() },
                    onTaskAdded = { taskViewModel.onTaskCreated(it) },
                    fontFamilyRobotoRegular
                )
                TaskList((uiState as TaskUIState.Success).task, fontFamilyRobotoRegular, taskViewModel, snackbarHostState)
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                )
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.End) {
                    FabDialog(
                        taskViewModel
                    )
                }
            }
        }
    }
}


@Composable
fun TaskList(
    task: List<TaskModel>,
    fontFamily: FontFamily,
    taskViewModel: TaskViewModel,
    snackbarHostState: SnackbarHostState
) {
    Column {
        Text(
            text = "Mis Tareas",
            fontSize = 26.sp,
            fontFamily = fontFamily,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LazyColumn(modifier = Modifier.padding(top = 10.dp, bottom = 70.dp)) {
            //Optimizacion de RV.
            items(task.reversed(), key = { it.id }) { task ->
                AnimatedItemTask(
                    taskModel = task,
                    taskViewModel = taskViewModel,
                    onItemRemoved = { taskViewModel.onTaskRemoved(task) },
                    snackbarHostState,
                    fontFamily
                )
            }
        }
    }
}


@Composable
fun AnimatedItemTask(
    taskModel: TaskModel,
    taskViewModel: TaskViewModel,
    onItemRemoved: () -> Unit, // Función para eliminar una tarea
    snackbarHostState: SnackbarHostState,
    fontFamily: FontFamily
) {
    val coroutineScope = rememberCoroutineScope()
    val visibleState = remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visibleState.value,
        enter = fadeIn(),
        exit = shrinkVertically() + fadeOut(animationSpec = tween(durationMillis = 500)),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                    })
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = taskModel.task,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    textDecoration = if (!visibleState.value) TextDecoration.LineThrough else TextDecoration.None
                )
                Checkbox(
                    checked = taskModel.selected,
                    onCheckedChange = {
                        taskViewModel.onCheckBoxSelected(taskModel)
                        visibleState.value = false

                        coroutineScope.launch {
                            // Inicia una animación cuando se elimina una tarea
                            delay(1000)
                            onItemRemoved()
                        }

                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "¡Tarea completada!",
                                actionLabel = "Deshacer",
                                duration = SnackbarDuration.Long
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    taskViewModel.onTaskCreated(taskModel.task)
                                }
                                SnackbarResult.Dismissed -> {
                                    taskViewModel.onTaskCreated(taskModel.task)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FabDialog(taskViewModel: TaskViewModel) {
    TextButton(
        onClick = {
            taskViewModel.onShowDialogToAddTask()
        }
    ) {
        Text(text = "Añadir Tarea", Modifier.padding(end = 10.dp), fontSize = 18.sp)
        Icon(imageVector = Icons.Filled.AddTask, contentDescription = "Añadir tarea")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onTaskAdded: (String) -> Unit,
    fontFamily: FontFamily
) {
    var myTask by remember {
        mutableStateOf("")
    }
    if (show) {
        BasicAlertDialog(onDismissRequest = { onDismiss() }, properties = DialogProperties(), modifier = Modifier.clip(
            RoundedCornerShape(24.dp)
        )) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = myTask,
                    onValueChange = { myTask = it },
                    label = { Text(text = "Añadir tarea:", fontFamily = fontFamily, fontSize = 18.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ))
                Spacer(modifier = Modifier.size(7.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.size(7.dp))
                Button(onClick = {
                    onTaskAdded(myTask)
                    myTask = ""
                }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Añadir", fontFamily = fontFamily, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun HorizontalLine(
    color: Color = Color.LightGray,
    height: androidx.compose.ui.unit.Dp = 1.dp
) {
    Box(
        modifier = Modifier
            .width(225.dp)
            .height(height)
            .background(color)
    )
}

