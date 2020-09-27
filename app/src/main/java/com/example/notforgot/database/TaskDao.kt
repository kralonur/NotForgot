package com.example.notforgot.database

import androidx.room.*
import com.example.notforgot.model.TaskDomain
import com.example.notforgot.model.db.items.DbTask
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAll(): Flow<List<DbTask>>

    @Transaction
    @Query("SELECT * FROM task")
    fun getAllDomain(): Flow<List<TaskDomain>>

    @Query("SELECT * FROM task WHERE category_id = (:id)")
    suspend fun getAllByCategoryId(id: Int): List<DbTask>

    @Query("SELECT * FROM task WHERE id = (:id)")
    suspend fun getTaskById(id: Int): DbTask

    @Transaction
    @Query("SELECT * FROM task WHERE id = (:id)")
    fun getTaskDomainById(id: Int): Flow<TaskDomain>

    @Update
    suspend fun update(task: DbTask)

    @Insert
    suspend fun insertAll(categories: List<DbTask>)

    @Insert
    suspend fun insertTask(task: DbTask): Long

    @Delete
    suspend fun delete(task: DbTask)
}