package com.example.notforgot.ui.register

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.authentication.register.Register
import com.example.notforgot.model.authentication.register.RegisterResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateLogin = MutableLiveData<Boolean?>()
    val navigateLogin: LiveData<Boolean?>
        get() = _navigateLogin

    private val _navigateMain = MutableLiveData<Boolean?>()
    val navigateMain: LiveData<Boolean?>
        get() = _navigateMain

    private val repo = AuthRepository()
    private val context = getApplication<Application>().applicationContext

    fun register(
        mail: String,
        name: String,
        pass: String,
    ): LiveData<ResultWrapper<RegisterResponse>> {
        val data = Register(mail, name, pass)
        return repo.register(data).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun completeRegister(registerResponse: RegisterResponse) {
        context.writeToken(registerResponse.token)
        Timber.i(registerResponse.token)
    }

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