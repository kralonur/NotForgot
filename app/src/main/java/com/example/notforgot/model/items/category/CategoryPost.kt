package com.example.notforgot.model.items.category

import com.squareup.moshi.Json

data class CategoryPost(
    @Json(name = "name") val name: String
)