package com.example.notforgot.model.db

import androidx.room.TypeConverter

class LogModelConverter {
    @TypeConverter
    fun fromLogModel(value: LogModel): String {
        return value.name
    }

    @TypeConverter
    fun toLogModel(value: String): LogModel {
        return when (value) {
            LogModel.CATEGORY.name -> LogModel.CATEGORY
            LogModel.TASK.name -> LogModel.TASK
            else -> throw IllegalArgumentException("Value invalid")
        }
    }
}