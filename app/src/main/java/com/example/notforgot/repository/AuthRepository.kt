package com.example.notforgot.repository

import com.example.notforgot.api.NetworkService
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.authentication.login.Login
import com.example.notforgot.model.authentication.register.Register
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepository {
    private val api = NetworkService.authService

    fun login(login: Login) = flow {
        emit(ResultWrapper.Loading)

        val response = api.login(login)
        emit(ResultWrapper.Success(response))
    }.catch {
        emit(ResultWrapper.Error)
    }

    fun register(register: Register) = flow {
        emit(ResultWrapper.Loading)

        val response = api.register(register)
        emit(ResultWrapper.Success(response))
    }.catch {
        emit(ResultWrapper.Error)
    }

}