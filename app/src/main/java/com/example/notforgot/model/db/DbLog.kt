package com.example.notforgot.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "log")
data class DbLog(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    @ColumnInfo(name = "type")
    @TypeConverters(LogTypeConverter::class)
    val logType: LogType,
    @ColumnInfo(name = "model")
    @TypeConverters(LogModelConverter::class)
    val logModel: LogModel,
    @ColumnInfo(name = "model_id") val model_id: Int,
)