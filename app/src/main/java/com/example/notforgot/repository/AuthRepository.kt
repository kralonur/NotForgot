package com.example.notforgot.repository

import android.content.Context
import com.example.notforgot.api.NetworkService
import com.example.notforgot.model.authentication.login.Login
import com.example.notforgot.model.authentication.register.Register

class AuthRepository(context: Context) : BaseRepository() {
    private val api = NetworkService.getAuthService(context)

    fun login(login: Login) = flowCall {
        api.login(login)
    }

    fun register(register: Register) = flowCall {
        api.register(register)
    }

}