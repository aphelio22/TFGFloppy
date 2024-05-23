package com.example.tfgfloppy.ui.screen.task

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.res.stringResource
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
import com.example.tfgfloppy.addTask.ui.TaskUIState
import com.example.tfgfloppy.addTask.viewmodel.TaskViewModel
import com.example.tfgfloppy.ui.model.taskModel.TaskModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MyTaskScreen(context: Context, taskViewModel: TaskViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val fontFamilyRobotoRegular = FontFamily(Font(R.font.roboto_regular))
    val showAddDialog: Boolean by taskViewModel.showAddDialog.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedItem = remember {
        mutableStateOf<TaskModel?>(null)
    }

    //Se manjea el estado de la interfaz de usuario.
    val uiState by produceState<TaskUIState>(
        initialValue = TaskUIState.Loading,
        key1 = lifecycle,
        key2 = taskViewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            taskViewModel.uiState.collect { value = it }
        }
    }

    //Se manjea el estado de la interfaz de usuario.
    when (uiState) {
        is TaskUIState.Error -> {
            Log.d("MyTaskScreen", "Something went wrong")
        }

        TaskUIState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.loadingMessage_TaskScreen))
            }
        }

        is TaskUIState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AddTaskDialog(
                    show = showAddDialog,
                    onDismiss = {
                        selectedItem.value = null
                        taskViewModel.dialogClose()
                    },
                    onTaskAdded = { taskViewModel.onTaskCreated(it) },
                    onTaskUpdated = { noteModel: TaskModel, updatedContent: String ->
                        taskViewModel.onTaskUpdated(noteModel, updatedContent)
                    },
                    fontFamily = fontFamilyRobotoRegular,
                    selectedItem = selectedItem,
                    context = context,
                    taskViewModel = taskViewModel
                )
                TaskList(
                    task = (uiState as TaskUIState.Success).task,
                    fontFamily = fontFamilyRobotoRegular,
                    taskViewModel = taskViewModel,
                    snackbarHostState = snackbarHostState,
                    context = context,
                    selectedItem = selectedItem
                )
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 60.dp)
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    FabDialog(
                        taskViewModel = taskViewModel,
                        fontFamily = fontFamilyRobotoRegular
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
    snackbarHostState: SnackbarHostState,
    context: Context,
    selectedItem: MutableState<TaskModel?>
) {
    Column {
        Text(
            text = stringResource(R.string.dailyTasks_TaskScreenTaskList),
            fontSize = 26.sp,
            fontFamily = fontFamily,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LazyColumn(modifier = Modifier.padding(top = 10.dp, bottom = 70.dp)) {
                                    //Optimización del RecyclerView.
            items(task.reversed(), key = { it.id }) { task ->
                AnimatedItemTask(
                    taskModel = task,
                    taskViewModel = taskViewModel,
                    onItemRemoved = { taskViewModel.onTaskRemoved(task) },
                    snackbarHostState = snackbarHostState,
                    fontFamily = fontFamily,
                    context = context,
                    selectedItem = selectedItem
                )
            }
        }
    }
}

@Composable
fun AnimatedItemTask(
    taskModel: TaskModel,
    taskViewModel: TaskViewModel,
    onItemRemoved: () -> Unit,
    snackbarHostState: SnackbarHostState,
    fontFamily: FontFamily,
    context: Context,
    selectedItem: MutableState<TaskModel?>
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
                        selectedItem.value = taskModel //Se coge el item seleccionado.
                        taskViewModel.fetchTaskContent(taskModel.id) //Se coge el contenido de la tarea de la base de datos para usarse en el EditText.
                        taskViewModel.onShowDialogToAddTask()
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
                    textDecoration = if (!visibleState.value) TextDecoration.LineThrough else TextDecoration.None //Se tacha el texto si el item no está visible.
                )
                Checkbox(
                    checked = taskModel.selected,
                    onCheckedChange = {
                        taskViewModel.onCheckBoxSelected(taskModel) //Se marca el checkbox.
                        visibleState.value = false

                        coroutineScope.launch { //Se ocula l atarea con una transición y después de elimina.
                            delay(1000)
                            onItemRemoved()
                        }

                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = context.getString(R.string.taskCompleted_TaskScreenSnackBar),
                                actionLabel = context.getString(R.string.undo_TaskScreenSnackBar),
                                duration = SnackbarDuration.Long
                            )
                            when (result) { //Snackbar para restaurar la tarea.
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
private fun FabDialog(taskViewModel: TaskViewModel, fontFamily: FontFamily) {
    TextButton(
        onClick = {
            taskViewModel.onShowDialogToAddTask()
        }
    ) {
        Text(
            text = stringResource(R.string.addTaskButtonText_TaskScreenAddTask),
            modifier = Modifier.padding(end = 10.dp),
            fontSize = 18.sp,
            fontFamily = fontFamily
        )
        Icon(
            imageVector = Icons.Filled.AddTask,
            contentDescription = stringResource(R.string.addTaskDescription_TaskScreenAddTask)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onTaskAdded: (String) -> Unit,
    fontFamily: FontFamily,
    selectedItem: MutableState<TaskModel?>,
    onTaskUpdated: (TaskModel, String) -> Unit,
    context: Context,
    taskViewModel: TaskViewModel
) {
    var myTask by remember {
        mutableStateOf(if (selectedItem.value != null) taskViewModel.taskContent.value ?: "" else "")
    }

    //Muestra el texto de la tarea seleccionada.
    LaunchedEffect(selectedItem.value) {
        myTask = if (selectedItem.value != null) taskViewModel.taskContent.value ?: "" else ""
    }

    //Maneja el estado del diálogo para agregar o editar tareas.
    if (show) {
        BasicAlertDialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(),
            modifier = Modifier.clip(
                RoundedCornerShape(24.dp)
            )
        ) {
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
                    label = {
                        if (selectedItem.value != null) { //Si hay algún item seleccionado el label del EditText pasa a: "Editar tarea:".
                            Text(
                                text = stringResource(R.string.editTask_TaskScreenAddtaskDialog),
                                fontFamily = fontFamily,
                                fontSize = 18.sp
                            )
                        } else { //Si no hay item seleccionado el label del EditText pasa a: "Añadir tarea:".
                            Text(
                                text = stringResource(R.string.addTaskTitle_TaskScreenAddTaskDialog),
                                fontFamily = fontFamily,
                                fontSize = 18.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.size(7.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.size(7.dp))
                Button(
                    onClick = {
                        if (selectedItem.value != null && myTask.isNotEmpty()) { //Si hay item seleccionado y el EditText no está vacío se muestra el texto de la tarea seleccionada para actualizarlo.
                            selectedItem.value?.let { task ->
                                onTaskUpdated(
                                    task,
                                    myTask
                                )
                                onDismiss()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.updatedTask_TaskScreen),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (myTask != "") { //Si el EditText no está vacío el texto que se escriba se agregará como una nueva tarea.
                            onTaskAdded(myTask)
                            myTask = ""
                            onDismiss()
                        }
                    }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.addTaskButtonText_AddTaskDialog
                        ),
                        fontFamily = fontFamily,
                        fontSize = 18.sp
                    )
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

