package com.example.notforgot.ui.login

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.login.Login
import com.example.notforgot.model.remote.authentication.login.LoginResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateRegister = MutableLiveData<Boolean?>()
    val navigateRegister: LiveData<Boolean?>
        get() = _navigateRegister

    private val _navigateMain = MutableLiveData<Boolean?>()
    val navigateMain: LiveData<Boolean?>
        get() = _navigateMain

    private val context = getApplication<Application>().applicationContext
    private val authRepo = AuthRepository(context)
    private val itemsRepo = ItemsRepository(context)

    fun login(mail: String, pass: String): LiveData<ResultWrapper<LoginResponse>> {
        val data = Login(mail, pass)
        return authRepo.login(data).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun completeLogin(loginResponse: LoginResponse) {
        context.writeToken(loginResponse.token)
        Timber.i(loginResponse.token)
    }

    fun loginUnsuccessful() {
        context.writeToken("")
    }

    fun fetchFromCloud() =
        itemsRepo.fetchFromCloud().asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

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