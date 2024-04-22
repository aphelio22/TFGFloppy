package com.example.navegacionconbotonflotante.composable.screens.noteScreen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navegacionconbotonflotante.model.Notes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNoteScreen(navController: NavController, context: Context) {
    val notes = remember {
        mutableStateListOf<Notes>()
    }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var (content, setContent) = remember { mutableStateOf("") }
    TextArea(content) { newContent ->
        setContent(newContent)
    }
    val selectedItem = remember {
        mutableStateOf<Notes?>(null)
    }

    MultiFAB(notes, content, setContent, selectedItem, context)
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
    notes: SnapshotStateList<Notes>, content: String, selectedItem: (String) -> Unit,
    setContent: MutableState<Notes?>,
    context: Context
) {
    var isVisible by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 0.dp, 10.dp, 20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Save(isVisible, notes, content, setContent, selectedItem)
        DeleteNotes(isVisible, selectedItem, setContent, notes)
        ShareNotes(isVisible, setContent, context, content)
        ShowNotes(isVisible, notes, setContent, selectedItem)
        Row(Modifier.padding(top = 20.dp)) {
            FloatingActionButton(onClick = {
                isVisible = !isVisible },
                modifier = Modifier.onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        isVisible = false
                    }
                }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ColumnScope.Save(
    isVisible: Boolean,
    notes: SnapshotStateList<Notes>,
    content: String,
    selectedItem: MutableState<Notes?>,
    setContent: (String) -> Unit
) {
    AnimatedVisibility(isVisible) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Guardar") },
            icon = { Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null) },
            onClick = {
                if (selectedItem.value != null) {
                    selectedItem.value?.content = content
                    selectedItem.value = null
                } else {
                    val newNote = Notes(content)
                    notes.add(newNote)
                }
                setContent("")
            },
            Modifier.padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun ColumnScope.DeleteNotes(
    isVisible: Boolean,
    setContent: (String) -> Unit,
    selectedItem: MutableState<Notes?>,
    notes: MutableList<Notes>
) {
    AnimatedVisibility(isVisible) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Eliminar nota") },
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) },
            onClick = {
                selectedItem.value?.let { note ->
                    notes.remove(note)
                    selectedItem.value = null
                    setContent("")
                }
            },
            Modifier.padding(bottom = 20.dp)
        )
    }
}

@Composable
private fun ColumnScope.ShareNotes(
    isVisible: Boolean,
    selectedItem: MutableState<Notes?>,
    context: Context,
    content: String
) {
    AnimatedVisibility(isVisible) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Compartir nota") },
            icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
            onClick = {
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
                    Toast.makeText(context, "No hay contenido a compartir", Toast.LENGTH_SHORT).show()
                }
            },
            Modifier.padding(bottom = 20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.ShowNotes(
    isVisible: Boolean, notes: SnapshotStateList<Notes>, selectedItem: MutableState<Notes?>,
    setContent: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    AnimatedVisibility(isVisible) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Ver notas") },
            icon = { Icon(imageVector = Icons.Default.Menu, contentDescription = null) },
            onClick = { showBottomSheet = !showBottomSheet })
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize()
                .heightIn(max = 300.dp)
        ) {
            NoteItemView(notes) { selectedNote ->
                selectedItem.value = selectedNote
                setContent(selectedNote.content)
                showBottomSheet = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
fun NoteItemView(notes: SnapshotStateList<Notes>, onItemClick: (Notes) -> Unit) {
    LazyRow(verticalAlignment = Alignment.CenterVertically) {
        items(notes) { note ->
            ItemNotes(note = note, onItemClick)
        }
    }
}

@Composable
fun ItemNotes(note: Notes, onItemClick: (Notes) -> Unit) {
    Card(border = BorderStroke(1.dp, Color.LightGray), modifier = Modifier
        .width(250.dp)
        .height(350.dp)
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


