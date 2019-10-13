package com.example.keykeeper.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.keykeeper.model.repo.MainRepo

class MainViewModel(private val mainRepo: MainRepo):ViewModel() {


    val tabTitle = MutableLiveData<List<String>>()

    fun getTitle(){
        tabTitle.value = mainRepo.getTitle()
        Log.v("TabTitleTest", "获取标题成功:${tabTitle.value}")
    }


}