package com.example.tfgfloppy.firebase.domain

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ResetPasswordUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return suspendCoroutine { continuation ->
            if (email.isEmpty()) {
                continuation.resume(Result.failure(Exception("El correo electrónico no puede estar vacío")))
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Result.success(Unit))
                        } else {
                            continuation.resume(Result.failure(task.exception ?: Exception("Error desconocido al restablecer la contraseña")))
                        }
                    }
            }
        }
    }
}