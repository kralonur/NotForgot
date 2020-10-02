package com.example.notforgot.model.db.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class DbTask(
    @PrimaryKey(autoGenerate = false) var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "done") var done: Int,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "deadline") var deadline: Long,
    @ColumnInfo(name = "category_id") var category_id: Int,
    @ColumnInfo(name = "priority_id") var priority_id: Int,
)