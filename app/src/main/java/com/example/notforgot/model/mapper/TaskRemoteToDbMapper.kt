package com.example.notforgot.model.mapper

import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.remote.items.task.Task

object TaskRemoteToDbMapper : Mapper<Task, DbTask> {
    override fun map(input: Task): DbTask {
        return DbTask(
            input.id,
            input.title,
            input.description,
            input.done,
            input.created,
            input.deadline,
            input.category.id,
            input.priority.id
        )
    }
}