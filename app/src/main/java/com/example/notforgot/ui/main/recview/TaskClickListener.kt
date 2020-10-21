package com.example.notforgot.ui.main.recview

import com.example.notforgot.model.db.items.DbTask

interface TaskClickListener {
    fun onClick(taskData: DbTask)

    fun onChecked(taskData: DbTask)
}