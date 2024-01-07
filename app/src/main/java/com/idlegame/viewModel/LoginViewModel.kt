package com.idlegame.viewModel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.idlegame.model.LoginModel
import com.google.firebase.auth.FirebaseUser

class LoginViewModel(private val loginModel: LoginModel) : ViewModel() {

    private val _loginResult = MutableLiveData<FirebaseUser?>()
    val loginResult: LiveData<FirebaseUser?>
        get() = _loginResult

    private val _loginError = MutableLiveData<Exception>()
    val loginError: LiveData<Exception>
        get() = _loginError

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    init {
        checkLoggedInStatus()
    }

    private fun checkLoggedInStatus() {
        _isLoggedIn.value = FirebaseAuth.getInstance().currentUser != null
    }

    fun handleLoginResult(data: Intent?) {
        loginModel.handleLoginInResult(data, { user ->
            _loginResult.value = user
            checkLoggedInStatus() // Update login status after handling the result
        }, { exception ->
            _loginError.value = exception
        })
    }

    fun getLoginIntent(): Intent {
        return loginModel.getLoginInIntent()
    }

    fun loginWithEmail(email: String, password: String) {
        loginModel.loginInWithEmail(email, password) { user, exception ->
            if (user != null) {
                _loginResult.value = user
            } else {
                _loginError.value = exception
            }
            checkLoggedInStatus() // Update login status after attempting login
        }
    }
}
