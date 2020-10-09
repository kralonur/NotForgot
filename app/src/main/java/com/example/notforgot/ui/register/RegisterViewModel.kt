package com.example.notforgot.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    private val _inputValidation = MutableLiveData<List<RegisterValidation>>()
    val inputValidation: LiveData<List<RegisterValidation>>
        get() = _inputValidation

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
    ) {
        if (validateInput(mail, name, pass, passRepeat)) register(mail, name, pass)
    }

    private fun validateInput(
        mail: String,
        name: String,
        pass: String,
        passRepeat: String,
    ): Boolean {
        val validationList = ArrayList<RegisterValidation>()

        if (name.isEmpty() || name.isBlank()) {
            validationList.add(RegisterValidation.EMPTY_NAME)
        }

        if (mail.isEmpty() || mail.isBlank()) {
            validationList.add(RegisterValidation.EMPTY_MAIL)
        } else if (!mail.isMail()) {
            validationList.add(RegisterValidation.INVALID_MAIL)
        }

        if (pass.isEmpty() || pass.isBlank()) {
            validationList.add(RegisterValidation.EMPTY_PASS)
        } else {
            if (pass != passRepeat) {
                validationList.add(RegisterValidation.NOT_SAME_PASS)
            }
        }

        _inputValidation.postValue(validationList)
        return validationList.isEmpty()
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