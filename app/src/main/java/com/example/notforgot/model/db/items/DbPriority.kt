package com.example.notforgot.model.db.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "priority")
data class DbPriority(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String,
)