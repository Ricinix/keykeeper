package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TitleRepo(private val keyDataBase: KeyDataBase) {


    fun getAllTitle(): Single<MutableList<TitleData>>{
        return keyDataBase.titleDao()
            .getAllTitle()
            .map {
                it.toMutableList()
            }
            .subscribeOn(Schedulers.io())
    }

    fun deleteTitle(name: String): Single<Int>{
        return keyDataBase.titleDao()
            .deleteByName(name)
            .subscribeOn(Schedulers.io())
    }

    fun addTitle(title: String, order: Int): Single<Long>{
        return keyDataBase.titleDao()
            .insertTitle(TitleData(title, order))
            .subscribeOn(Schedulers.io())
    }

    fun editTitle(newName: String, order: Int): Single<Int>{
        return keyDataBase.titleDao()
            .updateNameByOrder(newName, order)
            .subscribeOn(Schedulers.io())
    }

    fun editOrder(name:String, order: Int): Single<Int>{
        return keyDataBase.titleDao()
            .updateOrderByName(name, order)
            .subscribeOn(Schedulers.io())
    }

}