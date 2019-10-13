package com.example.keykeeper.model.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key_table")
data class KeyData (
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "account") val account: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "category") val category: String
)

data class KeySimplify(
    val id: Int,
    val name: String,
    val account: String,
    val password: String,
    val kind: String
)