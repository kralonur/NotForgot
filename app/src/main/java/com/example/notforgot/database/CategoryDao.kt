package com.example.notforgot.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notforgot.model.db.items.DbCategory

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAll(): List<DbCategory>

    @Query("SELECT * FROM category WHERE id = (:categoryId)")
    suspend fun getCategoryById(categoryId: Int): DbCategory

    @Insert
    suspend fun insertCategory(category: DbCategory)

    @Insert
    suspend fun insertAll(categories: List<DbCategory>)

    @Delete
    suspend fun delete(category: DbCategory)

}