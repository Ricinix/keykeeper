package com.example.keykeeper.di.module

import android.content.Context
import androidx.room.Room
import com.example.keykeeper.di.scope.AppScope
import com.example.keykeeper.model.room.KeyDataBase
import dagger.Module
import dagger.Provides

@Module
class BaseModule(private val applicationContext: Context) {

    @Provides @AppScope
    fun provideDataBase(): KeyDataBase {
        return Room.databaseBuilder(
            applicationContext,
            KeyDataBase::class.java,
            "key_database"
        ).build()
    }
}