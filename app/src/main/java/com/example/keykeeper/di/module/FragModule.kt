package com.example.keykeeper.di.module

import androidx.lifecycle.ViewModelProviders
import com.example.keykeeper.di.scope.FragmentScope
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.view.fragment.KeyFragment
import com.example.keykeeper.viewModel.FragViewModel
import com.example.keykeeper.viewModel.VMFactory.FragViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class FragModule(private val keyFragment: KeyFragment) {

    @Provides @FragmentScope
    fun provideFragViewModel(fragViewModelFactory: FragViewModelFactory): FragViewModel{
        return ViewModelProviders.of(keyFragment, fragViewModelFactory)[FragViewModel::class.java]
    }

    @Provides @FragmentScope
    fun provideFragRepo(keyDataBase: KeyDataBase): FragRepo{
        return FragRepo(keyDataBase)
    }
}