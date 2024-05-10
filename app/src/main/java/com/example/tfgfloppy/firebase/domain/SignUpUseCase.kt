package com.example.tfgfloppy.firebase.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SignUpUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): Result<FirebaseUser?> {
        return suspendCoroutine { continuation ->
            if (password != confirmPassword) {
                continuation.resume(Result.failure(Exception("Passwords do not match")))
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            continuation.resume(Result.success(user))
                        } else {
                            continuation.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                        }
                    }
            }
        }
    }
}