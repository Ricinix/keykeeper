package com.example.keykeeper.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.viewModel.FragViewModel
import javax.inject.Inject

class FragViewModelFactory @Inject constructor(private val fragRepo: FragRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FragViewModel(fragRepo) as T
    }
}