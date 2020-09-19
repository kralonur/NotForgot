package com.example.notforgot.model.items.task

import com.example.notforgot.model.items.priority.Priority
import com.example.notforgot.model.items.category.Category
import com.squareup.moshi.Json

data class Task(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "done") val done: Int,
    @Json(name = "deadline") val deadline: Long,
    @Json(name = "category") val category: Category,
    @Json(name = "priority") val priority: Priority,
    @Json(name = "created") val created: Long
)