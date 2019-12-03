package com.example.keykeeper.view

import android.app.Application
import com.example.keykeeper.di.component.BaseComponent
import com.example.keykeeper.di.component.DaggerBaseComponent
import com.example.keykeeper.di.module.BaseModule

class MyApplication:Application() {
    private lateinit var baseComponent: BaseComponent

    override fun onCreate() {
        super.onCreate()
        // 全局注射器（拿来给数据库inject的）
        baseComponent = DaggerBaseComponent.builder().baseModule(BaseModule(applicationContext)).build()
    }

    fun getBaseComponent(): BaseComponent{
        return baseComponent
    }

}