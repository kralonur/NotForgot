package com.example.notforgot.recview

import com.example.notforgot.model.items.task.Task

interface TaskClickListener {
    fun onClick(task_data: Task)
}