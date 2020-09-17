package com.example.notforgot.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _navigateRegister = MutableLiveData<Boolean?>()
    val navigateRegister: LiveData<Boolean?>
        get() = _navigateRegister

    private val _navigateMain = MutableLiveData<Boolean?>()
    val navigateMain: LiveData<Boolean?>
        get() = _navigateMain


    fun navigateToRegister() {
        _navigateRegister.postValue(true)
    }

    fun navigateToRegisterDone() {
        _navigateRegister.postValue(null)
    }

    fun navigateToMain() {
        _navigateMain.postValue(true)
    }

    fun navigateToMainDone() {
        _navigateMain.postValue(null)
    }
}