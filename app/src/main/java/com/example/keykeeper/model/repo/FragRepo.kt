package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FragRepo(private val keyDataBase: KeyDataBase) {

    fun getKeys(kind: String): Single<List<KeySimplify>>{
        return keyDataBase.keyDao().getByKind(kind)
            .subscribeOn(Schedulers.io())
    }
}