package com.example.keykeeper.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keykeeper.model.repo.MainRepo
import com.example.keykeeper.viewModel.MainViewModel
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val mainRepo: MainRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mainRepo) as T
    }
}