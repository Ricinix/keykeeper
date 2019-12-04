package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData

class MainRepo(private val keyDataBase: KeyDataBase) {

    suspend fun getTitle(): List<TitleData> {
        return keyDataBase.titleDao().getAllTitle()
    }

    suspend fun saveTitle(titleList: List<TitleData>) {
        keyDataBase.titleDao().insertAll(titleList)
    }

}