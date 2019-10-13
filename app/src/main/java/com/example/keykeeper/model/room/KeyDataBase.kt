package com.example.keykeeper.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.keykeeper.model.room.dao.KeyDao
import com.example.keykeeper.model.room.data.KeyData

@Database(entities = [KeyData::class], version = 1, exportSchema = false)
abstract class KeyDataBase: RoomDatabase() {
    abstract fun keyDao(): KeyDao
}