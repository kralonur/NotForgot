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
import com.example.notforgot.util.getNonNullValue
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

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val emailError = MutableLiveData("")
    val passwordError = MutableLiveData("")

    private fun login() {
        val data = Login(email.getNonNullValue(), password.getNonNullValue())

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                authRepo.login(data).collect {
                    if (it is ResultWrapper.Success) loginSuccessful(it.value)
                    _loginResponse.postValue(it)
                }
            }
        }
    }

    fun tryLogin() {
        if (validate()) login()
    }

    private fun validate(): Boolean {
        val mailValidation = validateEmail(email.getNonNullValue())
        val passValidation = validatePass(password.getNonNullValue())

        emailError.postValue(mailValidation)
        passwordError.postValue(passValidation)

        return mailValidation.isEmpty() && passValidation.isEmpty()
    }

    private fun validateEmail(mail: String): String {
        if (mail.isEmpty() || mail.isBlank()) {
            return getApplication<Application>().applicationContext.getString(R.string.mail_cannot_be_empty)
        } else if (!mail.isMail()) {
            return getApplication<Application>().applicationContext.getString(R.string.mail_is_not_valid)
        }

        return ""
    }

    private fun validatePass(pass: String): String {
        if (pass.isEmpty() || pass.isBlank()) {
            return getApplication<Application>().applicationContext.getString(R.string.password_cannot_be_empty)
        }

        return ""
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

