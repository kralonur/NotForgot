package com.example.notforgot.model.remote.authentication.register

import com.squareup.moshi.Json

data class Register(
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String,
    @Json(name = "password") val password: String,
)