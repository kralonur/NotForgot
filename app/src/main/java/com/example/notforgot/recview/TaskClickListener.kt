package com.example.notforgot.recview

import com.example.notforgot.model.db.items.DbTask

interface TaskClickListener {
    fun onClick(task_data: DbTask)
}