package com.example.notforgot.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.register.Register
import com.example.notforgot.model.remote.authentication.register.RegisterResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(getApplication<Application>().applicationContext)
    private val itemsRepo = ItemsRepository(getApplication<Application>().applicationContext)

    fun register(
        mail: String,
        name: String,
        pass: String,
    ): LiveData<ResultWrapper<RegisterResponse>> {
        val data = Register(mail, name, pass)
        return authRepo.register(data).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun completeRegister(registerResponse: RegisterResponse) {
        getApplication<Application>().applicationContext.writeToken(registerResponse.token)
        Timber.i(registerResponse.token)
    }

    fun registerUnsuccessful() {
        getApplication<Application>().applicationContext.writeToken("")
    }

    fun fetchFromCloud() =
        itemsRepo.fetchFromCloud().asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
}