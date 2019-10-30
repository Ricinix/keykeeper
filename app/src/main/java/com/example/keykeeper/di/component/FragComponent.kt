package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.FragModule
import com.example.keykeeper.di.scope.FragmentScope
import com.example.keykeeper.view.fragment.KeyFragment
import dagger.Component

@FragmentScope
@Component(modules = [FragModule::class], dependencies = [BaseComponent::class])
interface FragComponent {
    fun inject(keyFragment: KeyFragment)
}
