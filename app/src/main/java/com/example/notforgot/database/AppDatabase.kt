package com.example.notforgot.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notforgot.model.db.DbLog
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask

@Database(
    entities = [DbCategory::class, DbPriority::class, DbTask::class, DbLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "roomdb"
                    )
                        .build()
                }
            }

            return INSTANCE as AppDatabase
        }
    }

    abstract fun categoryDao(): CategoryDao
    abstract fun priorityDao(): PriorityDao
    abstract fun taskDao(): TaskDao
    abstract fun logDao(): LogDao
}