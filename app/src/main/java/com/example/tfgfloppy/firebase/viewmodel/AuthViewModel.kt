package com.example.tfgfloppy.firebase.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addNote.domain.AddNoteUseCase
import com.example.tfgfloppy.addNote.domain.DeleteAllNotesUseCase
import com.example.tfgfloppy.addNote.domain.DeleteNoteUseCase
import com.example.tfgfloppy.addNote.domain.GetNotesFromFirebaseUseCase
import com.example.tfgfloppy.firebase.domain.LogOutUseCase
import com.example.tfgfloppy.firebase.domain.LoginUseCase
import com.example.tfgfloppy.firebase.domain.ResetPasswordUseCase
import com.example.tfgfloppy.firebase.domain.SignUpUseCase
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel@Inject constructor(private val loginUseCase: LoginUseCase, private val signUpUseCase: SignUpUseCase, private val resetPasswordUseCase: ResetPasswordUseCase, private val logOutUseCase: LogOutUseCase, private val addNoteUseCase: AddNoteUseCase, private val deleteAllNotesUseCase: DeleteAllNotesUseCase, private val getNotesFromFirebaseUseCase: GetNotesFromFirebaseUseCase, private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore): ViewModel() {
    private val _loginResult = MutableLiveData<Result<FirebaseUser?>>()
    val loginResult: Flow<Result<FirebaseUser?>>
        get() = _loginResult.asFlow()

    private val _signUpResult = MutableLiveData<Result<FirebaseUser?>>()
    val signUpResult: LiveData<Result<FirebaseUser?>>
        get() = _signUpResult

    private val _resetPassResult = MutableLiveData<Result<Unit>>()
    val resetPassResult: LiveData<Result<Unit>>
        get() = _resetPassResult

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?>
        get() = _currentUser

    private val _showDialogToLogin = MutableLiveData<Boolean>()
    val showDialogToLogin: LiveData<Boolean>
        get() = _showDialogToLogin

    private val _showDialogToLogOut = MutableLiveData<Boolean>()
    val showDialogToLogOut: LiveData<Boolean>
        get() = _showDialogToLogOut

    private val _showDialogToSignUp = MutableLiveData<Boolean>()
    val showDialogToSignUp: LiveData<Boolean>
        get() = _showDialogToSignUp

    private val _showDialogToResetPassword = MutableLiveData<Boolean>()
    val showDialogToResetPassword: LiveData<Boolean>
        get() = _showDialogToResetPassword

    init {
        _currentUser.value = firebaseAuth.currentUser
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginUseCase.invoke(email, password)
            _loginResult.postValue(result)

            if (result.isSuccess) {
                updateCurrentUser()
            }
        }
    }

    fun isLoginInfoValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    fun loginWithFirebase(email: String, password: String) {
        if (isLoginInfoValid(email, password)) {
            login(email, password)
            getNotesFromFirestore()
        } else {
            // Manejar el caso en que la información de inicio de sesión no sea válida
            _loginResult.postValue(Result.failure(Exception("Información de inicio de sesión no válida")))
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = signUpUseCase.invoke(email, password, confirmPassword)
            _signUpResult.postValue(result)

            if (result.isSuccess) {
                updateCurrentUser()
            }
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutUseCase.invoke()
            updateCurrentUser()
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

    private fun updateCurrentUser() {
        _currentUser.postValue(firebaseAuth.currentUser)
    }

    fun onShowDialogToLogin() {
        _showDialogToLogin.value = true
    }

    fun onShowDialogToLogOut() {
        _showDialogToLogOut.value = true
    }

    fun onShowDialogToSignUp() {
        _showDialogToSignUp.value = true
    }

    fun onShowDialogToResetPassword() {
        _showDialogToResetPassword.value = true
    }

    fun dialogClose() {
        _showDialogToLogin.value = false
        _showDialogToLogOut.value = false
        _showDialogToSignUp.value = false
        _showDialogToResetPassword.value = false
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            deleteAllNotesUseCase.invoke()
        }
    }

    fun addNoteFromFireStore(note: NoteModel) {
        viewModelScope.launch {
            addNoteUseCase(NoteModel(content = note.content))
        }
    }

    fun addNoteToFirestore(note: NoteModel) {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            // Referencia a la colección de notas del usuario actual
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")

            // Obtener todas las notas existentes en la colección
            notesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    // Borrar todas las notas existentes en la colección
                    for (document in documents) {
                        document.reference.delete()
                    }
                    // Después de borrar todas las notas existentes, agregar la nueva nota
                    val noteData = hashMapOf(
                        "id" to note.id,
                        "content" to note.content
                    )

                    notesCollectionRef.add(noteData)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "Nueva nota agregada con el ID ${note.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.d("Firestore", "Error al agregar la nota: $e")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error al obtener las notas existentes: $e")
                }
        }
    }

    fun getNotesFromFirestore() {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            // Referencia a la colección de notas del usuario actual
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")

            // Obtener todas las notas existentes en la colección
            notesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    val notesList = mutableListOf<NoteModel>()
                    for (document in documents) {
                        val id = document.getLong("id")?.toInt()
                        val content = document.getString("content")
                        if (id != null && content != null) {
                            val note = NoteModel(id = id.toInt(), content = content)
                            notesList.add(note)
                        }
                    }
                    deleteAllNotes()
                    // Agregar las notas a Room
                    for (note in notesList) {
                        if (note.content != "") {
                            addNoteFromFireStore(note)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error getting documents: $e")
                }
        }
    }
}