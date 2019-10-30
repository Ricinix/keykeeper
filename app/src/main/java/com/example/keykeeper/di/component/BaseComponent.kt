package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.BaseModule
import com.example.keykeeper.di.scope.AppScope
import com.example.keykeeper.model.room.KeyDataBase
import dagger.Component

@AppScope
@Component(modules = [BaseModule::class])
interface BaseComponent {
    fun getKeyDataBase():KeyDataBase
}


