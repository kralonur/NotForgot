package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.DbLog
import com.example.notforgot.model.db.LogModel

@Dao
interface LogDao {

    @Query("SELECT * FROM log WHERE model = (:model)")
    suspend fun getAllByModel(model: LogModel): List<DbLog>

    @Query("SELECT * FROM log WHERE model_id = (:modelId) AND model = (:model)")
    suspend fun getLogByIdModel(modelId: Int, model: LogModel): DbLog?

    @Delete
    suspend fun delete(log: DbLog)

    @Insert
    suspend fun insert(log: DbLog)
}