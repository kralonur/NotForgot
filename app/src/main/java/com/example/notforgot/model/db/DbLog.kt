package com.example.notforgot.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log")
data class DbLog(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "model_id") val model_id: Int
)