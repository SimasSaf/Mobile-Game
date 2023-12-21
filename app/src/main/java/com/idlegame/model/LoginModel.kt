package com.idlegame.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginModel {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginInWithEmail(email: String, password: String, onComplete: (FirebaseUser?, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onComplete(user, null)
                } else {
                    onComplete(null, task.exception)
                }
            }
    }
}