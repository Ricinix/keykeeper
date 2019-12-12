package com.example.keykeeper.model.repo

import com.example.keykeeper.domain.Encipher
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData

class MainRepo(private val keyDataBase: KeyDataBase, private val encipher: Encipher) {

    suspend fun getTitle(): List<TitleData> {
        val data = keyDataBase.titleDao().getAllTitle()
        encipher.unlockTitleList(data)
        return data
    }

    suspend fun saveTitle(titleList: List<TitleData>) {
        encipher.lockTitleList(titleList)
        keyDataBase.titleDao().insertAll(titleList)
    }

}