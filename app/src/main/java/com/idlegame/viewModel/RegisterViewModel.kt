package com.idlegame.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idlegame.model.RegisterUserModel
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val registerUserModel = RegisterUserModel()
    private val _registrationStatus = MutableLiveData<String>()

    val registrationStatus: LiveData<String>
        get() = _registrationStatus

    fun registerUser(email: String, password: String, repeatedPassword: String) {
        viewModelScope.launch {
            registerUserModel.registerUser(email, password, repeatedPassword) { success, message ->
                if (success) {
                    _registrationStatus.postValue("Registration successful")
                } else {
                    _registrationStatus.postValue(message)
                }
            }
        }
    }
}
