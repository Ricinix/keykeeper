package com.example.keykeeper.di.module

import androidx.lifecycle.ViewModelProviders
import com.example.keykeeper.di.scope.ActivityScope
import com.example.keykeeper.domain.Encipher
import com.example.keykeeper.model.repo.MainRepo
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.view.activity.MainActivity
import com.example.keykeeper.viewModel.MainViewModel
import com.example.keykeeper.viewModel.viewModelFactory.MainViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class MainModule(private val mainActivity: MainActivity) {

    @Provides @ActivityScope
    fun provideMainViewModel(mainViewModelFactory:MainViewModelFactory): MainViewModel{
        return ViewModelProviders.of(mainActivity, mainViewModelFactory)[MainViewModel::class.java]
    }

    @Provides @ActivityScope
    fun provideMainRepo(keyDataBase: KeyDataBase, encipher: Encipher): MainRepo{
        return MainRepo(keyDataBase,encipher)
    }


}