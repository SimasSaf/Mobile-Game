package com.idlegame.viewModel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idlegame.model.GoogleSignInModel
import com.idlegame.model.LoginModel
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(private val googleSignInModel: GoogleSignInModel) : ViewModel() {

    private val loginModel = LoginModel()

    private val _loginResult = MutableLiveData<FirebaseUser?>()
    val loginResult: LiveData<FirebaseUser?>
        get() = _loginResult

    private val _loginError = MutableLiveData<Exception>()
    val loginError: LiveData<Exception>
        get() = _loginError
    fun handleLoginResult(data: Intent?) {
        googleSignInModel.handleSignInResult(data, { user ->
            _loginResult.value = user
        }, { exception ->
            _loginError.value = exception
        })
    }
    fun getLoginIntent(): Intent {
        return googleSignInModel.getSignInIntent()
    }
    fun loginWithEmail(email: String, password: String) {
        loginModel.loginInWithEmail(email, password) { user, exception ->
            if (user != null) {
                _loginResult.value = user
            } else {
                _loginError.value = exception
            }
        }
    }
}
