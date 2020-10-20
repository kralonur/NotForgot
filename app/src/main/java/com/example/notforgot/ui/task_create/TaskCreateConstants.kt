package com.example.notforgot.ui.task_create

import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority

object TaskCreateConstants {
    val EMPTY_CATEGORY = DbCategory(Int.MIN_VALUE, "")
    val EMPTY_PRIORITY = DbPriority(Int.MIN_VALUE, "", "")
    const val EMPTY_DEADLINE = Long.MIN_VALUE
    const val CREATE_TASK_ID = 0
}