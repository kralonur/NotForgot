package com.example.notforgot.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _navigateLogin = MutableLiveData<Boolean?>()
    val navigateLogin: LiveData<Boolean?>
        get() = _navigateLogin

    private val _navigateMain = MutableLiveData<Boolean?>()
    val navigateMain: LiveData<Boolean?>
        get() = _navigateMain


    fun navigateToLogin() {
        _navigateLogin.postValue(true)
    }

    fun navigateToLoginDone() {
        _navigateLogin.postValue(null)
    }

    fun navigateToMain() {
        _navigateMain.postValue(true)
    }

    fun navigateToMainDone() {
        _navigateMain.postValue(null)
    }
}