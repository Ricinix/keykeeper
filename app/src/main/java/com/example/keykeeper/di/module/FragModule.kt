package com.example.keykeeper.di.module

import androidx.lifecycle.ViewModelProviders
import androidx.room.Database
import androidx.room.Room
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.view.fragment.KeyFragment
import com.example.keykeeper.viewModel.FragViewModel
import com.example.keykeeper.viewModel.VMFactory.FragViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FragModule(private val keyFragment: KeyFragment) {

    @Provides @Singleton
    fun provideFragViewModel(fragViewModelFactory: FragViewModelFactory): FragViewModel{
        return ViewModelProviders.of(keyFragment, fragViewModelFactory)[FragViewModel::class.java]
    }

    @Provides @Singleton
    fun provideFragRepo(keyDataBase: KeyDataBase): FragRepo{
        return FragRepo(keyDataBase)
    }

    @Provides @Singleton
    fun provideDataBase(): KeyDataBase{
        return Room.databaseBuilder(
            keyFragment.activityAbove.applicationContext,
            KeyDataBase::class.java,
            "key_database"
        ).build()
    }
}