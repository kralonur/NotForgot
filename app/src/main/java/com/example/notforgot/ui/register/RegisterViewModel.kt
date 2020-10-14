package com.example.notforgot.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.R
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.register.Register
import com.example.notforgot.model.remote.authentication.register.RegisterResponse
import com.example.notforgot.repository.AuthRepository
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.isMail
import com.example.notforgot.util.writeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(getApplication<Application>().applicationContext)
    private val itemsRepo = ItemsRepository(getApplication<Application>().applicationContext)

    private val _fetchResponse = MutableLiveData<ResultWrapper<Unit>>()
    val fetchResponse: LiveData<ResultWrapper<Unit>>
        get() = _fetchResponse

    private val _registerResponse = MutableLiveData<ResultWrapper<RegisterResponse>>()
    val registerResponse: LiveData<ResultWrapper<RegisterResponse>>
        get() = _registerResponse

    private fun register(
        mail: String,
        name: String,
        pass: String,
    ) {
        val data = Register(mail, name, pass)
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                authRepo.register(data).collect {
                    if (it is ResultWrapper.Success) registerSuccessful(it.value)
                    _registerResponse.postValue(it)
                }
            }
        }
    }

    fun tryRegister(
        mail: String,
        name: String,
        pass: String,
        passRepeat: String,
        validation: RegisterValidation
    ) {
        if (validateInput(mail, name, pass, passRepeat, validation)) register(mail, name, pass)
    }

    private fun validateInput(
        mail: String,
        name: String,
        pass: String,
        passRepeat: String,
        validation: RegisterValidation
    ): Boolean {
        var valid = true

        if (name.isEmpty() || name.isBlank()) {
            valid = false
            validation.validateName(getApplication<Application>().applicationContext.getString(R.string.name_cannot_be_empty))
        }

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
        } else {
            if (pass != passRepeat) {
                valid = false
                validation.validatePassword(
                    getApplication<Application>().applicationContext.getString(
                        R.string.passwords_should_be_same
                    )
                )
            }
        }

        return valid
    }

    private fun registerSuccessful(registerResponse: RegisterResponse) {
        getApplication<Application>().applicationContext.writeToken(registerResponse.token)
        fetchFromCloud()
    }

    private fun registerUnsuccessful() {
        getApplication<Application>().applicationContext.writeToken("")
    }

    private fun fetchFromCloud() {
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                itemsRepo.fetchFromCloud().collect {
                    if (it is ResultWrapper.Error || it is ResultWrapper.ServerError) registerUnsuccessful()
                    _fetchResponse.postValue(it)
                }
            }
        }
    }
}