package com.example.notforgot.model.remote.items.category

import com.squareup.moshi.Json

data class CategoryPost(
    @Json(name = "name") val name: String,
)