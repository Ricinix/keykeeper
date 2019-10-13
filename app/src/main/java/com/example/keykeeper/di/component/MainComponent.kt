package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.MainModule
import com.example.keykeeper.view.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}