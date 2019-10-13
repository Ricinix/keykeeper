package com.example.keykeeper.di.module

import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModelProviders
import com.example.keykeeper.model.repo.MainRepo
import com.example.keykeeper.view.activity.MainActivity
import com.example.keykeeper.viewModel.MainViewModel
import com.example.keykeeper.viewModel.VMFactory.MainViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule(private val mainActivity: MainActivity) {

    @Provides @Singleton
    fun provideMainViewModel(mainViewModelFactory:MainViewModelFactory): MainViewModel{
        return ViewModelProviders.of(mainActivity, mainViewModelFactory)[MainViewModel::class.java]
    }

    @Provides @Singleton
    fun provideMainRepo(): MainRepo{
        return MainRepo(mainActivity.getSharedPreferences("setting", MODE_PRIVATE),
            mainActivity.getSharedPreferences("title", MODE_PRIVATE))
    }

}