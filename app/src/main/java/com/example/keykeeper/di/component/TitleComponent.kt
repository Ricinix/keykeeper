package com.example.keykeeper.di.component

import com.example.keykeeper.di.module.TitleModule
import com.example.keykeeper.di.scope.FragmentScope
import com.example.keykeeper.view.fragment.TitleSettingFragment
import dagger.Component

@FragmentScope
@Component(modules = [TitleModule::class], dependencies = [BaseComponent::class])
interface TitleComponent {
    fun inject(titleSettingFragment: TitleSettingFragment)
}