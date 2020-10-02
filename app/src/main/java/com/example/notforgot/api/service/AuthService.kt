package com.example.notforgot.api.service

import com.example.notforgot.model.remote.authentication.login.Login
import com.example.notforgot.model.remote.authentication.login.LoginResponse
import com.example.notforgot.model.remote.authentication.register.Register
import com.example.notforgot.model.remote.authentication.register.RegisterResponse
import com.example.notforgot.util.Constants
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST(Constants.Api.LOGIN)
    suspend fun login(
        @Body login: Login,
    ): LoginResponse

    @POST(Constants.Api.REGISTER)
    suspend fun register(
        @Body register: Register,
    ): RegisterResponse
}