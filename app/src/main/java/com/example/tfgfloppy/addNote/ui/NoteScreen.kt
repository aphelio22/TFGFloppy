package com.example.tfgfloppy.addNote.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.tfgfloppy.R
import com.example.tfgfloppy.addTask.ui.HorizontalLine
import com.example.tfgfloppy.ui.model.noteModel.NoteModel

@Composable
fun MyNoteScreen(context: Context, noteViewModel: NoteViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val showAddDialog: Boolean by noteViewModel.showAddDialog.observeAsState(false)
    remember {
        mutableStateListOf<NoteModel>()
    }

    val (content, setContent) = remember { mutableStateOf("") }
    TextArea(content) { newContent ->
        setContent(newContent)
    }

    val selectedItem = remember {
        mutableStateOf<NoteModel?>(null)
    }

    val fontFamilyRobotoRegular = FontFamily(Font(R.font.roboto_regular))

    val uiState by produceState<NoteUIState>(
        initialValue = NoteUIState.Loading,
        key1 = lifecycle,
        key2 = noteViewModel
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            noteViewModel.uiState.collect { value = it }
        }
    }

    when (uiState) {
        is NoteUIState.Error -> {
            TODO()
        }
        NoteUIState.Loading -> {
            CircularProgressIndicator()
        }
        is NoteUIState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MultiFAB(content,
                    setContent,
                    selectedItem,
                    context,
                    fontFamilyRobotoRegular,
                    noteViewModel,
                    uiState as NoteUIState.Success,
                    onNoteAdded = { noteViewModel.addNote(it) },
                    onNoteUpdated = { noteModel: NoteModel, updatedContent: String ->
                        noteViewModel.updateNote(noteModel, updatedContent)
                    }
                )
                AddTaskDialog(
                    show = showAddDialog,
                    onDismiss = { noteViewModel.dialogClose() },
                    selectedItem,
                    setContent,
                    onNoteDeleted = { noteModel: NoteModel ->
                        noteViewModel.deleteNote(noteModel)
                    },
                    fontFamily = fontFamilyRobotoRegular
                )
            }
        }
    }


}

@Composable
private fun TextArea(
    content: String,
    onContentChange: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MainTextArea(content, onContentChange)
    }
}

@Composable
private fun MultiFAB(
    content: String,
    selectedItem: (String) -> Unit,
    setContent: MutableState<NoteModel?>,
    context: Context,
    fontFamily: FontFamily,
    noteViewModel: NoteViewModel,
    uiState: NoteUIState.Success,
    onNoteAdded: (String) -> Unit,
    onNoteUpdated: (NoteModel, String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 10.dp, 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(Modifier.padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            SaveNotes(content, setContent, selectedItem,  onNoteAdded = onNoteAdded, onNoteUpdated = onNoteUpdated)
            DeleteNotes(noteViewModel)
            ShareNotes(setContent, context, content)
            ShowNotes(fontFamily, setContent, selectedItem, uiState)
        }
    }
}

@Composable
private fun ShareNotes(
    selectedItem: MutableState<NoteModel?>,
    context: Context,
    content: String
) {
    TextButton(onClick = {
        val selectedNote = selectedItem.value
        if (selectedNote != null) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, selectedNote.content)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Muestra un selector de aplicaciones para que el usuario elija dónde compartir
            context.startActivity(Intent.createChooser(intent, "Compartir nota"))
        } else if (content != "") {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, content)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Muestra un selector de aplicaciones para que el usuario elija dónde compartir
            context.startActivity(Intent.createChooser(intent, "Compartir nota"))
        } else {
            Toast.makeText(context, "No hay contenido a compartir", Toast.LENGTH_SHORT)
                .show()
        }
    }, modifier = Modifier.padding(end = 10.dp)) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null)
    }
}

@Composable
private fun DeleteNotes(
    noteViewModel: NoteViewModel
) {
    TextButton(onClick = {
        noteViewModel.onShowDialogToAddTask()
    }) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowNotes(
    fontFamily: FontFamily,
    selectedItem: MutableState<NoteModel?>,
    setContent: (String) -> Unit,
    uiState: NoteUIState.Success
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedButton(
        onClick = { showBottomSheet = !showBottomSheet },
        modifier = Modifier.padding(start = 30.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Ver Notas",
            fontFamily = fontFamily,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 10.dp)
        )
        Icon(imageVector = Icons.Default.Menu, contentDescription = null)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            modifier = Modifier
                .fillMaxSize()
                .heightIn(max = 300.dp)
        ) {
            Text(text = "Mis Notas", textAlign = TextAlign.Center, fontSize = 26.sp ,modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp))
            
            if (uiState.note.isEmpty()) {
                Text(text = "No hay notas", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
            } else {
                NoteItemView(uiState.note) { selectedNote ->
                    selectedItem.value = selectedNote
                    setContent(selectedNote.content)
                    showBottomSheet = false
                }
            }
        }
    }
}

@Composable
private fun SaveNotes(
    content: String,
    selectedItem: MutableState<NoteModel?>,
    setContent: (String) -> Unit,
    onNoteAdded: (String) -> Unit,
    onNoteUpdated: (NoteModel, String) -> Unit
) {
    TextButton(onClick = {
        if (selectedItem.value != null) {
            selectedItem.value?.let { note ->
                onNoteUpdated(
                    note,
                    content
                ) // Llama a onNoteUpdated para actualizar la nota existente
                selectedItem.value = null
            }
        } else {
            onNoteAdded(content)
        }
        setContent("")
    }, modifier = Modifier.padding(start = 15.dp)) {
        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
    }
}

@Composable
fun MainTextArea(content: String, onValueChanged: (String) -> Unit) {
    var offset by remember { mutableFloatStateOf(0f) }
    OutlinedTextField(
        value = content,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Transparent,
            unfocusedBorderColor = Transparent,
        ),
        onValueChange = { onValueChanged(it) }, modifier = Modifier
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            )
            .padding(bottom = 100.dp, start = 10.dp, end = 10.dp)
    )
}

@Composable
fun NoteItemView(notes: List<NoteModel>, onItemClick: (NoteModel) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(notes.reversed(), key =  {it.id }) { note ->
            ItemNotes(note = note, onItemClick)
        }
    }
}

@Composable
fun ItemNotes(note: NoteModel, onItemClick: (NoteModel) -> Unit) {
    Card(modifier = Modifier
        .width(275.dp)
        .height(300.dp)
        .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        .clickable { onItemClick(note) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.content,
                fontSize = 16.sp, // Ajusta este valor según sea necesario
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    selectedItem: MutableState<NoteModel?>,
    setContent: (String) -> Unit,
    onNoteDeleted: (NoteModel) -> Unit,
    fontFamily: FontFamily
) {
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
                Text(text = "¿Deseas eliminar la tarea?", fontFamily = fontFamily, fontSize = 18.sp)
                Spacer(modifier = Modifier.size(7.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.size(7.dp))
                Button(onClick = {
                    selectedItem.value?.let { note ->
                        onNoteDeleted(note)
                        setContent("")
                    }
                    setContent("")
                    onDismiss()
                }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Sí", fontFamily = fontFamily, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = {
                    onDismiss()
                }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "No", fontFamily = fontFamily, fontSize = 18.sp)
                }
            }
        }
    }
}



