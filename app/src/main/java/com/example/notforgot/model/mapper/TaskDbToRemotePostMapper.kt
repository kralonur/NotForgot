package com.example.notforgot.model.mapper

import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.remote.items.task.TaskPost

object TaskDbToRemotePostMapper : Mapper<DbTask, TaskPost> {
    override fun map(input: DbTask): TaskPost {
        return TaskPost(
            input.title,
            input.description,
            input.done,
            input.deadline,
            input.category_id,
            input.priority_id
        )
    }
}