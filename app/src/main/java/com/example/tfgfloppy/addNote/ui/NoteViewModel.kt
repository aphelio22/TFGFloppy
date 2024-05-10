package com.example.tfgfloppy.addNote.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addNote.domain.AddNoteUseCase
import com.example.tfgfloppy.addNote.domain.DeleteNoteUseCase
import com.example.tfgfloppy.addNote.domain.GetNoteUseCase
import com.example.tfgfloppy.addNote.domain.UpdateNoteUseCase
import com.example.tfgfloppy.firebase.domain.LoginUseCase
import com.example.tfgfloppy.firebase.domain.ResetPasswordUseCase
import com.example.tfgfloppy.firebase.domain.SignUpUseCase
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val addNoteUseCase: AddNoteUseCase, private val deleteNoteUseCase: DeleteNoteUseCase, private val updateNoteUseCase: UpdateNoteUseCase, getNoteUseCase: GetNoteUseCase, private val loginUseCase: LoginUseCase, private val signUpUseCase: SignUpUseCase, private val resetPasswordUseCase: ResetPasswordUseCase): ViewModel() {

    val uiState: StateFlow<NoteUIState> = getNoteUseCase().map (NoteUIState::Success)
        .catch { NoteUIState.Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteUIState.Loading)

    private val _showDialogToDeleteNotes = MutableLiveData<Boolean>()
    val showDialogToDeleteNotes: LiveData<Boolean>
        get() = _showDialogToDeleteNotes

    private val _showDialogToLogin = MutableLiveData<Boolean>()
    val showDialogToLogin: LiveData<Boolean>
        get() = _showDialogToLogin

    private val _loginResult = MutableLiveData<Result<FirebaseUser?>>()
    val loginResult: LiveData<Result<FirebaseUser?>>
        get() = _loginResult

    private val _signUpResult = MutableLiveData<Result<FirebaseUser?>>()
    val signUpResult: LiveData<Result<FirebaseUser?>>
        get() = _signUpResult

    private val _resetPassResult = MutableLiveData<Result<Unit>>()
    val resetPassResult: LiveData<Result<Unit>>
        get() = _resetPassResult

    fun addNote(note: String) {
        viewModelScope.launch {
            addNoteUseCase(NoteModel(content = note))
        }
    }
    fun updateNote(noteModel: NoteModel, content: String) {
        viewModelScope.launch {
            updateNoteUseCase(noteModel.copy(content = content))
        }
    }

    fun deleteNote(noteModel: NoteModel) {
        viewModelScope.launch {
            deleteNoteUseCase(noteModel)
        }
    }

    fun onShowDialogToDeleteNotes() {
        _showDialogToDeleteNotes.value = true
    }

    fun onShowDialogToLogin() {
        _showDialogToLogin.value = true
    }

    fun dialogClose() {
        _showDialogToDeleteNotes.value = false
        _showDialogToLogin.value = false
        //_showEditDialog.value = false
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginUseCase.invoke(email, password)
            _loginResult.postValue(result)
        }
    }

    fun isLoginInfoValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    fun loginWithFirebase(email: String, password: String) {
        if (isLoginInfoValid(email, password)) {
            login(email, password)
        } else {
            // Manejar el caso en que la información de inicio de sesión no sea válida
            // Puedes emitir un estado de error o mostrar un mensaje al usuario
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = signUpUseCase.invoke(email, password, confirmPassword)
            _signUpResult.postValue(result)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = resetPasswordUseCase.invoke(email)
            _resetPassResult.postValue(result)
        }
    }

    fun isEmailValid(email: String): Boolean {
        return email.isNotEmpty()
    }
}