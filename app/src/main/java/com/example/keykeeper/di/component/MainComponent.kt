package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.MainModule
import com.example.keykeeper.di.scope.ActivityScope
import com.example.keykeeper.view.activity.MainActivity
import dagger.Component

@ActivityScope
@Component(modules = [MainModule::class], dependencies = [BaseComponent::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}
