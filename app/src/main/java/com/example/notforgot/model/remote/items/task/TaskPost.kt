package com.example.notforgot.model.remote.items.task

import com.squareup.moshi.Json

data class TaskPost(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "done") val done: Int,
    @Json(name = "deadline") val deadline: Long,
    @Json(name = "category_id") val categoryId: Int,
    @Json(name = "priority_id") val priorityId: Int,
)