package com.example.tfgfloppy.firebase.domain

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LogOutUseCase @Inject constructor(private val firebaseAuth: FirebaseAuth) {
    operator fun invoke() {
        firebaseAuth.signOut()
    }
}