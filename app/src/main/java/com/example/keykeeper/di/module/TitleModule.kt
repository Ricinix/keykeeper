package com.example.keykeeper.di.module

import androidx.lifecycle.ViewModelProviders
import com.example.keykeeper.di.scope.FragmentScope
import com.example.keykeeper.domain.Encipher
import com.example.keykeeper.model.repo.TitleRepo
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.view.fragment.TitleSettingFragment
import com.example.keykeeper.viewModel.TitleViewModel
import com.example.keykeeper.viewModel.viewModelFactory.TitleViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class TitleModule(private val titleSettingFragment: TitleSettingFragment) {

    @Provides @FragmentScope
    fun provideTitleViewModel(titleRepo: TitleRepo): TitleViewModel{
        return ViewModelProviders.of(titleSettingFragment, TitleViewModelFactory(titleRepo))[TitleViewModel::class.java]
    }

    @Provides @FragmentScope
    fun provideTitleRepo(keyDataBase: KeyDataBase, encipher: Encipher): TitleRepo{
        return TitleRepo(keyDataBase, encipher)
    }
}