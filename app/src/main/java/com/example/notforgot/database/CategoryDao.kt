package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.items.DbCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<DbCategory>>

    @Query("SELECT * FROM category WHERE id = (:categoryId)")
    suspend fun getCategoryById(categoryId: Int): DbCategory

    @Insert
    suspend fun insertCategory(category: DbCategory): Long

    @Insert
    suspend fun insertAll(categories: List<DbCategory>)

    @Delete
    suspend fun delete(category: DbCategory)

}