package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.FragModule
import com.example.keykeeper.view.fragment.KeyFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FragModule::class])
interface FragComponent {
    fun inject(keyFragment: KeyFragment)
}