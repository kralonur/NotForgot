package com.example.notforgot.model.mapper

import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.remote.items.priority.Priority

object PriorityRemoteToDbMapper : Mapper<Priority, DbPriority> {
    override fun map(input: Priority): DbPriority {
        return DbPriority(input.id, input.name, input.color)
    }
}