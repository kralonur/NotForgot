package com.example.notforgot.model.remote.items.task

import com.example.notforgot.model.remote.items.category.Category
import com.example.notforgot.model.remote.items.priority.Priority
import com.squareup.moshi.Json

data class Task(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "done") val done: Int = 0,
    @Json(name = "deadline") val deadline: Long = 0,
    @Json(name = "category") val category: Category,
    @Json(name = "priority") val priority: Priority,
    @Json(name = "created") val created: Long = 0,
)