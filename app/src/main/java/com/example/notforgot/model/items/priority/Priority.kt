package com.example.notforgot.model.items.priority

import com.squareup.moshi.Json

data class Priority(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "color") val color: String
)