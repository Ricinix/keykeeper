package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FragRepo(private val keyDataBase: KeyDataBase) {

    fun getTitleByOrder(order: Int): Single<String>{
        return keyDataBase.titleDao().getTitleByOrder(order)
            .map { it.name }
            .subscribeOn(Schedulers.io())
    }

    fun getKeysByOrder(order: Int): Single<List<KeySimplify>>{
        return keyDataBase.keyDao().getByOrder(order)
            .subscribeOn(Schedulers.io())
    }

    fun getKeysByCategory(category: String): Single<List<KeySimplify>>{
        return keyDataBase.keyDao().getByKind(category)
            .subscribeOn(Schedulers.io())
    }

    fun storeKeys(keyData: KeyData): Single<Long>{
        return keyDataBase.keyDao().insertKeyData(keyData)
            .subscribeOn(Schedulers.io())
    }

    fun deleteKeys(id: Int): Single<Int>{
        return keyDataBase.keyDao().deleteById(id)
            .subscribeOn(Schedulers.io())
    }

    fun updateKeys(keysData: KeyData): Single<Int>{
        return keyDataBase.keyDao().updateKeyData(keysData)
            .subscribeOn(Schedulers.io())
    }
}