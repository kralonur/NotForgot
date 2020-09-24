package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.DbLog

@Dao
interface LogDao {
    @Query("SELECT * FROM log")
    suspend fun getAll(): List<DbLog>

    @Query("SELECT * FROM log WHERE model_id = (:modelId)")
    suspend fun getLogByModelId(modelId: Int): DbLog

    @Delete
    suspend fun delete(log: DbLog)

    @Insert
    suspend fun insert(log: DbLog)
}