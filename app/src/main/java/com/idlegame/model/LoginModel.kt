package com.idlegame.model

import InventoryDAO
import UserDAO
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

class LoginModel(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleLoginInClient: GoogleSignInClient
    private val userDAO = UserDAO()
    private val inventoryDAO = InventoryDAO()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleLoginInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getLoginInIntent(): Intent {
        return googleLoginInClient.signInIntent
    }

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

    fun handleLoginInResult(data: Intent?, onSuccess: (FirebaseUser) -> Unit, onFailure: (Exception?) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let { firebaseUser ->
                            // Check for inventory existence and create if not exists
                            inventoryDAO.checkInventoryExists(firebaseUser.uid) { exists ->
                                if (!exists) {
                                    inventoryDAO.createInventory(firebaseUser.uid, firebaseUser.email ?: "") { success, errorMessage ->
                                        if (!success) {
                                            Log.e("LoginModel", "Failed to create inventory: $errorMessage")
                                        }
                                    }
                                }
                            }

                            // Handle user data
                            userDAO.getUserData(firebaseUser.email ?: "") { userData, _ ->
                                if (userData == null) {
                                    userDAO.saveNewUserData(firebaseUser.uid, firebaseUser.email ?: "") { success, _ ->
                                        if (success) onSuccess(firebaseUser)
                                    }
                                } else {
                                    onSuccess(firebaseUser)
                                }
                            }
                        }
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
            // Login out from Firebase
            auth.signOut()

            // Login out from Google Login-In
            googleLoginInClient.signOut().addOnCompleteListener {
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
