package com.example.keykeeper.di.module

import android.content.Context
import androidx.room.Room
import com.example.keykeeper.di.scope.AppScope
import com.example.keykeeper.domain.Encipher
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

    @Provides @AppScope
    fun provideEncipher(): Encipher{
        val sharedPreferences = applicationContext.getSharedPreferences("password", Context.MODE_PRIVATE)
        val corePassword = sharedPreferences.getString("core_pwd", "123456")?:"123546"
        return Encipher(corePassword)
    }
}