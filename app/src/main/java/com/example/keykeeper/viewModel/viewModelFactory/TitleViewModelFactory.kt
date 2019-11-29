package com.example.keykeeper.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keykeeper.model.repo.TitleRepo
import com.example.keykeeper.viewModel.TitleViewModel
import javax.inject.Inject

class TitleViewModelFactory @Inject constructor(private val titleRepo: TitleRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TitleViewModel(titleRepo) as T
    }
}