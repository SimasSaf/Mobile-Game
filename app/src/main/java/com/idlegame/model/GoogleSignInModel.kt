package com.idlegame.model

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.idlegame.R

class GoogleSignInModel(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(data: Intent?, onSuccess: (FirebaseUser) -> Unit, onFailure: (Exception?) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.let { onSuccess(it) }
                    } else {
                        onFailure(task.exception)
                    }
                }
        } catch (e: ApiException) {
            onFailure(e)
        }
    }

    fun logOutUser(onComplete: (Boolean) -> Unit) {
        try {
            // Sign out from Firebase
            auth.signOut()

            // Sign out from Google Sign-In
            googleSignInClient.signOut().addOnCompleteListener {
                onComplete(it.isSuccessful)
            }
        } catch (e: Exception) {
            // Handle exception
            onComplete(false)
        }
    }

    fun signedInUser(): FirebaseUser? {
        return auth.currentUser
    }
}
