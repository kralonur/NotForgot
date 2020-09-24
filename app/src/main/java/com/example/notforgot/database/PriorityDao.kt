package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.items.DbPriority

@Dao
interface PriorityDao {

    @Query("SELECT * FROM priority")
    suspend fun getAll(): List<DbPriority>

    @Query("SELECT * FROM priority WHERE id = (:priorityId)")
    suspend fun getPriorityById(priorityId: Int): DbPriority

    @Query("SELECT * FROM priority WHERE name = (:name)")
    suspend fun getPriorityByName(name: String): DbPriority

    @Insert
    suspend fun insertAll(priorities: List<DbPriority>)

}