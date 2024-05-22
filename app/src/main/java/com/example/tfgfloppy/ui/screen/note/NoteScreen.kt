package com.example.tfgfloppy.ui.screen.note

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.res.stringResource
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
import com.example.tfgfloppy.addNote.ui.NoteUIState
import com.example.tfgfloppy.addNote.viewmodel.NoteViewModel
import com.example.tfgfloppy.constants.Constants
import com.example.tfgfloppy.ui.screen.task.HorizontalLine
import com.example.tfgfloppy.firebase.viewmodel.AuthViewModel
import com.example.tfgfloppy.ui.model.noteModel.NoteModel

@Composable
fun MyNoteScreen(context: Context, noteViewModel: NoteViewModel, authViewModel: AuthViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val showDeleteDialog: Boolean by noteViewModel.showDialogToDeleteNotes.observeAsState(false)
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
            Log.d("MyTaskScreen", "Something went wrong")
        }

        NoteUIState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.loadingMessage_NoteScreen))
            }
        }

        is NoteUIState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                MultiButton(
                    content = content,
                    selectedItem = setContent,
                    setContent = selectedItem,
                    context = context,
                    fontFamily = fontFamilyRobotoRegular,
                    noteViewModel = noteViewModel,
                    authViewModel = authViewModel,
                    uiState = (uiState as NoteUIState.Success),
                    onNoteAdded = { noteViewModel.addNote(it) },
                    onNoteUpdated = { noteModel: NoteModel, updatedContent: String ->
                        noteViewModel.updateNote(noteModel, updatedContent)
                    },
                )
                DeleteNoteContentDialog(
                    show = showDeleteDialog,
                    onDismiss = { noteViewModel.dialogClose() },
                    selectedItem = selectedItem,
                    setContent = setContent,
                    onNoteDeleted = { noteModel: NoteModel ->
                        noteViewModel.deleteNote(noteModel)
                    },
                    fontFamily = fontFamilyRobotoRegular,
                    authViewModel = authViewModel
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MainTextArea(
            content = content,
            onValueChanged = onContentChange
        )
    }
}

@Composable
private fun MultiButton(
    content: String,
    selectedItem: (String) -> Unit,
    setContent: MutableState<NoteModel?>,
    context: Context,
    fontFamily: FontFamily,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel,
    uiState: NoteUIState.Success,
    onNoteAdded: (String) -> Unit,
    onNoteUpdated: (NoteModel, String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 20.dp, end = 10.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        SaveNotes(
            content = content,
            setContent,
            selectedItem,
            onNoteAdded = onNoteAdded,
            onNoteUpdated = onNoteUpdated,
            context = context
        )
        DeleteNotes(
            noteViewModel = noteViewModel,
            content = content,
            context = context
        )
        ShareNotes(
            setContent,
            context = context,
            content = content
        )
        AccountManagement(authViewModel = authViewModel)
    }
    Row(
        Modifier
            .padding(top = 20.dp, start = 10.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        ShowNotes(
            fontFamily = fontFamily,
            setContent,
            selectedItem,
            uiState = uiState,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun AccountManagement(authViewModel: AuthViewModel) {
    val currentUser by authViewModel.currentUser.observeAsState()
    TextButton(onClick = {
        if (currentUser == null) {
            authViewModel.onShowDialogToLogin()
        } else {
            authViewModel.onShowDialogToLogOut()
        }
    }) {
        Icon(
            Icons.Filled.AccountCircle,
            contentDescription = stringResource(R.string.accountManagementDescription_NoteScreen)
        )
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
        if ((selectedNote != null)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, selectedNote.content)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(intent, Constants.SHARE_NOTE_TITLE))
        } else if (content != "") {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, content)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(intent, Constants.SHARE_NOTE_TITLE))
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.contentNotAvaible_NoteScreenShareNote),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = stringResource(R.string.shareNoteDescription_NoteScreen)
        )
    }
}

@Composable
private fun DeleteNotes(
    noteViewModel: NoteViewModel,
    content: String,
    context: Context
) {
    TextButton(onClick = {
        if (content.isNotEmpty()) {
            noteViewModel.onShowDialogToDeleteNotes()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.noContentToDelete_NoteScreenDeleteNote),
                Toast.LENGTH_SHORT
            ).show()
        }
    }) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.deleteNoteDescription_NoteScreenDeleteNote)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowNotes(
    fontFamily: FontFamily,
    selectedItem: MutableState<NoteModel?>,
    setContent: (String) -> Unit,
    uiState: NoteUIState.Success,
    authViewModel: AuthViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    TextButton(
        onClick = { showBottomSheet = !showBottomSheet }
    ) {
        Text(
            text = stringResource(R.string.showNotes_NoteScreenShowNotes),
            fontFamily = fontFamily,
            fontSize = 18.sp,
            modifier = Modifier.padding(end = 10.dp)
        )
        Icon(
            imageVector = Icons.Filled.EditNote,
            contentDescription = stringResource(R.string.showNotesDescription_NoteScreenShowNotes)
        )
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
            if (authViewModel.currentUser.value != null) {
                if (uiState.note.isEmpty()) {
                    authViewModel.getNotesFromFirestore()
                } else {
                    authViewModel.addNoteToFirestore(uiState.note)
                }
            }
            Text(
                text = stringResource(R.string.myNotes_NoteScreenBottomSheet),
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (uiState.note.isEmpty()) {
                Text(
                    text = stringResource(R.string.emptyNotes_NoteScreenBottomSheet),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
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
    onNoteUpdated: (NoteModel, String) -> Unit,
    context: Context
) {
    TextButton(onClick = {
        if (selectedItem.value != null && content.isNotEmpty()) {
            selectedItem.value?.let { note ->
                onNoteUpdated(
                    note,
                    content
                )
                selectedItem.value = null
                Toast.makeText(
                    context,
                    context.getString(R.string.noteUpdated_NoteScreenUpdatedNote),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (content.isNotEmpty()) {
            onNoteAdded(content)
            Toast.makeText(
                context,
                context.getString(R.string.noteSaved_NoteScreenSaveNote), Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.noContentToSave_NoteScreenSaveNote), Toast.LENGTH_SHORT
            ).show()
        }
        setContent("")
    }, modifier = Modifier.padding(start = 15.dp)) {
        Icon(Icons.Filled.Save, contentDescription = null)
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
        onValueChange = { onValueChanged(it) },
        modifier = Modifier
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(notes.reversed(), key = { it.id }) { note ->
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
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteNoteContentDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    selectedItem: MutableState<NoteModel?>,
    setContent: (String) -> Unit,
    onNoteDeleted: (NoteModel) -> Unit,
    fontFamily: FontFamily,
    authViewModel: AuthViewModel
) {
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
                Text(
                    text = stringResource(R.string.discardNote_NoteScreenDeleteNote),
                    fontFamily = fontFamily,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.size(7.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.size(7.dp))
                Row {
                    Button(
                        onClick = {
                            selectedItem.value?.let { note ->
                                onNoteDeleted(note)
                                authViewModel.deleteNoteFromFirestore(note)
                            }
                            selectedItem.value = null
                            setContent("")
                            onDismiss()
                        }, modifier = Modifier
                            .padding(end = 10.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.acceptDiscardNote_NoteScreenDeleteNote),
                            fontFamily = fontFamily,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Button(
                        onClick = {
                            onDismiss()
                        }, modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.cancelDiscard_NoteScreenDeleteNote),
                            fontFamily = fontFamily,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}







