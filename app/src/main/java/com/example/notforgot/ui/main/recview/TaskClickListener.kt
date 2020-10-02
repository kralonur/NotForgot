package com.example.notforgot.ui.main.recview

import com.example.notforgot.model.db.items.DbTask

interface TaskClickListener {
    fun onClick(task_data: DbTask)

    fun onChecked(task_data: DbTask)
}