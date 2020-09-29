package com.example.notforgot.model

import com.example.notforgot.model.db.items.DbCategory

data class RecviewItem(
    val isTask: Boolean,
    val category: DbCategory? = null,
    val task: TaskDomain? = null
)