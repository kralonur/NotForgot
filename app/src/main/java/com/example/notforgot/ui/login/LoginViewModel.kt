package com.example.notforgot.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.login.Login
import com.example.notforgot.model.remote.authentication.login.LoginResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(getApplication<Application>().applicationContext)
    private val itemsRepo = ItemsRepository(getApplication<Application>().applicationContext)

    fun login(mail: String, pass: String): LiveData<ResultWrapper<LoginResponse>> {
        val data = Login(mail, pass)
        return authRepo.login(data).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun completeLogin(loginResponse: LoginResponse) {
        getApplication<Application>().applicationContext.writeToken(loginResponse.token)
        Timber.i(loginResponse.token)
    }

    fun loginUnsuccessful() {
        getApplication<Application>().applicationContext.writeToken("")
    }

    fun fetchFromCloud() =
        itemsRepo.fetchFromCloud().asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

}