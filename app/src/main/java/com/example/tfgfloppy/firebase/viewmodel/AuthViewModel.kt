package com.example.tfgfloppy.firebase.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.addNote.domain.AddAllNotesUseCase
import com.example.tfgfloppy.firebase.domain.LogOutUseCase
import com.example.tfgfloppy.firebase.domain.LoginUseCase
import com.example.tfgfloppy.firebase.domain.ResetPasswordUseCase
import com.example.tfgfloppy.firebase.domain.SignUpUseCase
import com.example.tfgfloppy.ui.model.noteModel.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val addAllNotesUseCase: AddAllNotesUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
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

    private fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginUseCase.invoke(email, password)
            _loginResult.postValue(result)

            if (result.isSuccess) {
                updateCurrentUser()
            }
        }
    }

    private fun isLoginInfoValid(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    fun loginWithFirebase(email: String, password: String) {
        if (isLoginInfoValid(email, password)) {
            login(email, password)
        } else {
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

    fun addNotesFromFirestore(notesList: List<NoteModel>) {
        viewModelScope.launch {
            addAllNotesUseCase(notesList)
        }
    }

    fun addNoteToFirestore(notes: List<NoteModel>) {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")
            notesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Nota eliminada con ID ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.d("Firestore", "Error al eliminar la nota: $e")
                            }
                    }
                    for (note in notes) {
                        val noteData = hashMapOf(
                            "id" to note.id,
                            "content" to note.content
                        )
                        notesCollectionRef.add(noteData)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Nueva nota agregada con el ID ${note.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.d("Firestore", "Error al agregar la nota: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error al obtener las notas: $e")
                }
        }
    }

    fun getNotesFromFirestore() {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")
            notesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    val notesList = mutableListOf<NoteModel>()
                    for (document in documents) {
                        val id = document.getLong("id")?.toInt()
                        val content = document.getString("content")
                        if (id != null && content != null) {
                            val note = NoteModel(id = id, content = content)
                            notesList.add(note)
                        }
                    }
                    addNotesFromFirestore(notesList)
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error getting documents: $e")
                }
        }
    }

    fun deleteNoteFromFirestore(note: NoteModel) {
        val user = firebaseAuth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val notesCollectionRef = firestore.collection("user").document(userId)
                .collection("note")
            notesCollectionRef.whereEqualTo("id", note.id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Nota eliminada de Firestore con ID ${note.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.d("Firestore", "Error al eliminar la nota de Firestore: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Firestore", "Error al obtener la nota de Firestore: $e")
                }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
    }
}