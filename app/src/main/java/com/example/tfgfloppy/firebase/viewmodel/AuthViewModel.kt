package com.example.tfgfloppy.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.tfgfloppy.firebase.domain.LogOutUseCase
import com.example.tfgfloppy.firebase.domain.LoginUseCase
import com.example.tfgfloppy.firebase.domain.ResetPasswordUseCase
import com.example.tfgfloppy.firebase.domain.SignUpUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel@Inject constructor(private val loginUseCase: LoginUseCase, private val signUpUseCase: SignUpUseCase, private val resetPasswordUseCase: ResetPasswordUseCase, private val logOutUseCase: LogOutUseCase, private val firebaseAuth: FirebaseAuth): ViewModel() {
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

    fun updateCurrentUser() {
        _currentUser.postValue(firebaseAuth.currentUser)
    }

    fun onShowDialogToLogin() {
        _showDialogToLogin.value = true
    }

    fun onShowDialogToLogOut() {
        _showDialogToLogOut.value = true
    }

    fun dialogClose() {
        _showDialogToLogin.value = false
        _showDialogToLogOut.value = false
    }
}