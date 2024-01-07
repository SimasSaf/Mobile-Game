package com.idlegame.model

import UserDAO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterUserModel {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDAO: UserDAO = UserDAO()

    fun registerUser(email: String, password: String, repeatedPassword: String, callback: (Boolean, String) -> Unit) {
        val checkRegisterInputsResponse = checkRegisterInputs(email, password, repeatedPassword)
        if (checkRegisterInputsResponse != "Valid") {
            callback(false, checkRegisterInputsResponse)
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    userDAO.saveNewUserData(user?.uid ?: "", email) { success, message ->
                        if (success) {
                            callback(true, "Registration successful")
                        } else {
                            callback(false, message)
                        }
                    }
                } else {
                    val errorMessage = when (val exception = task.exception) {
                        is FirebaseAuthWeakPasswordException -> "Password is too weak"
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                        is FirebaseAuthUserCollisionException -> "Email already in use"
                        else -> "Registration failed: ${exception?.message}"
                    }
                    callback(false, errorMessage)
                }
            }
    }

    private fun checkRegisterInputs(email: String, password: String, repeatedPassword: String): String {
        if (!checkEmailValidity(email)) return "Invalid email format"
        if (!checkPasswordValidity(password)) return "Password does not meet criteria"
        if (!checkPasswordsMatching(password, repeatedPassword)) return "Passwords do not match"
        return "Valid"
    }

    private fun checkEmailValidity(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkPasswordValidity(password: String): Boolean {
        return password.length >= 3
    }

    private fun checkPasswordsMatching(password: String, repeatedPassword: String): Boolean {
        return password == repeatedPassword
    }
}
