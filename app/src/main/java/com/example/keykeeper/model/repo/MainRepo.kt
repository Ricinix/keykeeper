package com.example.keykeeper.model.repo

import android.util.Log
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainRepo(
    private val keyDataBase: KeyDataBase
){

    fun getTitle(): Single<List<TitleData>>{
        return keyDataBase.titleDao()
            .getAllTitle()
            .subscribeOn(Schedulers.io())
    }

    fun saveTitle(titleList: List<TitleData>){
        keyDataBase.titleDao()
            .insertAll(titleList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<Long>>{
                override fun onSuccess(t: List<Long>) {
                    Log.v("DataBaseTest", "succeed in inserting $t titles")
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

}