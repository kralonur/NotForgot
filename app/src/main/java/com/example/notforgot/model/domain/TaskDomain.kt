package com.example.notforgot.model.domain

import androidx.room.Embedded
import androidx.room.Relation
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask

data class TaskDomain(
    @Embedded val task: DbTask,
    @Relation(
        parentColumn = "priority_id",
        entityColumn = "id"
    ) val priority: DbPriority,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    ) val category: DbCategory,
)