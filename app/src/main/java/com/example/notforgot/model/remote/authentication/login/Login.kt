package com.example.notforgot.model.remote.authentication.login

import com.squareup.moshi.Json

data class Login(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
)