package com.example.keykeeper.model.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key_table")
data class KeyData (
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "account") val account: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "kind") val kind: String
)

data class KeySimplify(
    val name: String,
    val account: String,
    val password: String
)