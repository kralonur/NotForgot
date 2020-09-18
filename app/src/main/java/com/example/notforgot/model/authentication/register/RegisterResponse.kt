package com.example.notforgot.model.authentication.register

import com.squareup.moshi.Json

data class RegisterResponse(
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String,
    @Json(name = "id") val id: Int,
    @Json(name = "api_token") val token: String
)