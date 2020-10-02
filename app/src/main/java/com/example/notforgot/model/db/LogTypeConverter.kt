package com.example.notforgot.model.db

import androidx.room.TypeConverter

class LogTypeConverter {

    @TypeConverter
    fun fromLogType(value: LogType): String {
        return value.name
    }

    @TypeConverter
    fun toLogType(value: String): LogType {
        return when (value) {
            LogType.INSERT.name -> LogType.INSERT
            LogType.UPDATE.name -> LogType.UPDATE
            LogType.DELETE.name -> LogType.DELETE
            else -> throw IllegalArgumentException("Value invalid")
        }
    }
}