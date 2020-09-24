package com.example.notforgot.database

import androidx.room.*
import com.example.notforgot.model.db.items.DbTask

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<DbTask>

    @Query("SELECT * FROM task WHERE Id = (:id)")
    suspend fun getTaskById(id: Int): DbTask

    @Update
    suspend fun update(task: DbTask)

    @Insert
    suspend fun insertAll(categories: List<DbTask>)

    @Insert
    suspend fun insertTask(task: DbTask): Long

    @Delete
    suspend fun delete(task: DbTask)
}