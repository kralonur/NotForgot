package com.example.notforgot.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.R
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.login.Login
import com.example.notforgot.model.remote.authentication.login.LoginResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.isMail
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(getApplication<Application>().applicationContext)
    private val itemsRepo = ItemsRepository(getApplication<Application>().applicationContext)

    private val _fetchResponse = MutableLiveData<ResultWrapper<Unit>>()
    val fetchResponse: LiveData<ResultWrapper<Unit>>
        get() = _fetchResponse

    private val _loginResponse = MutableLiveData<ResultWrapper<LoginResponse>>()
    val loginResponse: LiveData<ResultWrapper<LoginResponse>>
        get() = _loginResponse

    private fun login(mail: String, pass: String) {
        val data = Login(mail, pass)

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                authRepo.login(data).collect {
                    if (it is ResultWrapper.Success) loginSuccessful(it.value)
                    _loginResponse.postValue(it)
                }
            }
        }
    }

    fun tryLogin(mail: String, pass: String, validation: LoginValidation) {
        if (validateInput(mail, pass, validation)) login(mail, pass)
    }

    private fun validateInput(
        mail: String,
        pass: String,
        validation: LoginValidation
    ): Boolean {
        var valid = true

        if (mail.isEmpty() || mail.isBlank()) {
            valid = false
            validation.validateEmail(getApplication<Application>().applicationContext.getString(R.string.mail_cannot_be_empty))
        } else if (!mail.isMail()) {
            valid = false
            validation.validateEmail(getApplication<Application>().applicationContext.getString(R.string.mail_is_not_valid))
        }

        if (pass.isEmpty() || pass.isBlank()) {
            valid = false
            validation.validatePassword(getApplication<Application>().applicationContext.getString(R.string.password_cannot_be_empty))
        }

        return valid
    }

    private fun loginSuccessful(loginResponse: LoginResponse) {
        getApplication<Application>().applicationContext.writeToken(loginResponse.token)
        fetchFromCloud()
    }

    private fun loginUnsuccessful() {
        getApplication<Application>().applicationContext.writeToken("")
    }

    private fun fetchFromCloud() {
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                itemsRepo.fetchFromCloud().collect {
                    if (it is ResultWrapper.Error || it is ResultWrapper.ServerError) loginUnsuccessful()
                    _fetchResponse.postValue(it)
                }
            }
        }
    }

}

