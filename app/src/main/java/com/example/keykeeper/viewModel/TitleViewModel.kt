package com.example.keykeeper.viewModel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.*
import com.example.keykeeper.domain.SingleLiveEvent
import com.example.keykeeper.model.repo.TitleRepo
import com.example.keykeeper.model.room.data.TitleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TitleViewModel(private val titleRepo: TitleRepo) : ViewModel() {

    val titles = MutableLiveData<MutableList<TitleData>>()

    val wrongMsg = SingleLiveEvent<Int>()

    fun getAllTitle() = viewModelScope.launch {
        val t = withContext(Dispatchers.IO) {
            titleRepo.getAllTitle()
        }
        Log.v("DataBaseTest", "Title Setting get $t")
        titles.value = t.also { it.add(TitleData("", t.size)) }
    }


    fun deleteTitle(titleData: TitleData) = viewModelScope.launch {
        val t = withContext(Dispatchers.IO) {
            titleRepo.deleteTitle(titleData)
        }
        Log.v("DataBaseTest", "succeed in updating ${titleData.name}, $t")
        getAllTitle()
    }


    fun addTitle(titleName: String) = viewModelScope.launch {
        val t = withContext(Dispatchers.IO) {
            titleRepo.addTitle(TitleData(titleName, titles.value?.lastIndex ?: 0) )
        }
        if (t == -1L) {
//            wrongMsg.enable = true
//            wrongMsg.value = INSERT_CONFLICT
            wrongMsg.setValue(INSERT_CONFLICT)
            Log.v("DataBaseTest", "insertFail for conflict")
        } else {
            Log.v("DataBaseTest", "succeed in inserting $titleName, $t")
            getAllTitle()
        }
    }


    fun editTitle(titleData: TitleData) = viewModelScope.launch {
        Log.v("DataBaseTest", "updating the $titleData")
        try {
            withContext(Dispatchers.IO) {
                titleRepo.editTitle(titleData)
            }
            getAllTitle()
        }catch (e : SQLiteConstraintException){
//            wrongMsg.enable = true
//            wrongMsg.value = INSERT_CONFLICT
            wrongMsg.setValue(INSERT_CONFLICT)
        }

    }


    fun updateAllTitles(titleList: List<TitleData>) = viewModelScope.launch(Dispatchers.IO) {
        for (index in titleList.indices) {
            titleRepo.editOrder(titleList[index])
        }
    }

    companion object {
        const val INSERT_CONFLICT = 0
        const val TAG = "LiveDataTest"
    }



}