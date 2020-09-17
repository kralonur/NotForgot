package com.example.notforgot.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {

    private val _navigateLogin = MutableLiveData<Boolean?>()
    val navigateLogin: LiveData<Boolean?>
        get() = _navigateLogin


    fun navigateToLogin() {
        _navigateLogin.postValue(true)
    }

    fun navigateToLoginDone() {
        _navigateLogin.postValue(null)
    }
}