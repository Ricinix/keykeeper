package com.example.keykeeper.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.keykeeper.model.repo.MainRepo
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainViewModel(private val mainRepo: MainRepo):ViewModel() {


    val tabTitle = MutableLiveData<List<TitleData>>()

    fun getTitle(){
        Log.v("DataBaseTest", "searching the database")
        mainRepo.getTitle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<TitleData>>{
                override fun onSuccess(t: List<TitleData>) {
                    Log.v("DataBaseTest", "get the titleList: $t")
                    if (t.isEmpty()){
                        val newList = mutableListOf<TitleData>().also {
                            it.add(TitleData("工作", 0))
                            it.add(TitleData("学习", 1))
                        }
                        saveTitle(newList)
                        tabTitle.value = newList
                    }else{
                        tabTitle.value = t
                    }
                    Log.v("TabTitleTest", "获取标题成功:${tabTitle.value}")
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    private fun saveTitle(titleList: List<TitleData>){
        mainRepo.saveTitle(titleList)
    }


}