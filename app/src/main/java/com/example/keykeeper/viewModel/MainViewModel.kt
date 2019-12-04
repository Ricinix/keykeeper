package com.example.keykeeper.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keykeeper.model.repo.MainRepo
import com.example.keykeeper.model.room.data.TitleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val mainRepo: MainRepo) : ViewModel() {


    val tabTitle = MutableLiveData<List<TitleData>>()

    fun getTitle() = viewModelScope.launch {
        Log.v("DataBaseTest", "searching the database")
        val t = withContext(Dispatchers.IO) {
            mainRepo.getTitle()
        }
        Log.v("DataBaseTest", "get the titleList: $t")
        if (t.isEmpty()) {
            val newList = mutableListOf<TitleData>().also {
                it.add(TitleData("工作", 0))
                it.add(TitleData("学习", 1))
            }
            saveTitle(newList)
            tabTitle.value = newList
        } else {
            tabTitle.value = t
        }
        Log.v("TabTitleTest", "获取标题成功:${tabTitle.value}")
    }


    private fun saveTitle(titleList: List<TitleData>) = viewModelScope.launch(Dispatchers.IO) {
        mainRepo.saveTitle(titleList)
    }


}