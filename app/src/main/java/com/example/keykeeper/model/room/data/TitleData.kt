package com.example.keykeeper.model.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "title_table")
data class TitleData(
    @PrimaryKey
    @ColumnInfo(name = "title_name")
    var name: String,
    @ColumnInfo(name = "title_order")
    var order: Int
)