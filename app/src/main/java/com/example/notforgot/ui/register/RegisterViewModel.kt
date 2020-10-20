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
import com.example.notforgot.util.getNonNullValue
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

    val email = MutableLiveData("")
    val name = MutableLiveData("")
    val password = MutableLiveData("")
    val passwordRepeat = MutableLiveData("")

    val emailError = MutableLiveData("")
    val nameError = MutableLiveData("")
    val passwordError = MutableLiveData("")
    val passwordRepeatError = MutableLiveData("")

    private fun register() {
        val data =
            Register(email.getNonNullValue(), name.getNonNullValue(), password.getNonNullValue())
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                authRepo.register(data).collect {
                    if (it is ResultWrapper.Success) registerSuccessful(it.value)
                    _registerResponse.postValue(it)
                }
            }
        }
    }

    fun tryRegister() {
        if (validate()) register()
    }

    private fun validate(): Boolean {
        val mailValidation = validateEmail(email.getNonNullValue())
        val nameValidation = validateName(name.getNonNullValue())
        val passValidation = validatePass(password.getNonNullValue())
        val passRepeatValidation =
            validatePassRepeat(password.getNonNullValue(), passwordRepeat.getNonNullValue())

        emailError.postValue(mailValidation)
        nameError.postValue(nameValidation)
        passwordError.postValue(passValidation)
        passwordRepeatError.postValue(passRepeatValidation)

        return mailValidation.isEmpty() && nameValidation.isEmpty()
                && passValidation.isEmpty() && passRepeatValidation.isEmpty()
    }

    private fun validateEmail(mail: String): String {
        if (mail.isEmpty() || mail.isBlank()) {
            return getApplication<Application>().applicationContext.getString(R.string.mail_cannot_be_empty)
        } else if (!mail.isMail()) {
            return getApplication<Application>().applicationContext.getString(R.string.mail_is_not_valid)
        }

        return ""
    }

    private fun validateName(name: String): String {
        if (name.isEmpty() || name.isBlank()) {
            return getApplication<Application>().applicationContext.getString(R.string.name_cannot_be_empty)
        }

        return ""
    }

    private fun validatePass(pass: String): String {
        if (pass.isEmpty() || pass.isBlank()) {
            return getApplication<Application>().applicationContext.getString(R.string.password_cannot_be_empty)
        }

        return ""
    }

    private fun validatePassRepeat(pass: String, passRepeat: String): String {
        if (pass != passRepeat) {
            return getApplication<Application>().applicationContext.getString(R.string.passwords_should_be_same)
        }

        return ""
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