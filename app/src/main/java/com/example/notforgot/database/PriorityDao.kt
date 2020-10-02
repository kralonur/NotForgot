package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.items.DbPriority
import kotlinx.coroutines.flow.Flow

@Dao
interface PriorityDao {

    @Query("SELECT * FROM priority")
    fun getAll(): Flow<List<DbPriority>>

    @Insert
    suspend fun insertAll(priorities: List<DbPriority>)

}