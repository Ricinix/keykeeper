package com.example.keykeeper.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.keykeeper.model.room.dao.KeyDao
import com.example.keykeeper.model.room.dao.TitleDao
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.TitleData

@Database(entities = [KeyData::class, TitleData::class], version = 1, exportSchema = false)
abstract class KeyDataBase: RoomDatabase() {
    abstract fun keyDao(): KeyDao

    abstract fun titleDao(): TitleDao
}