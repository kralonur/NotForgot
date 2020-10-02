package com.example.notforgot.model.remote.authentication.login

import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "api_token") val token: String,
)