package com.example.keykeeper.model.repo

import com.example.keykeeper.domain.Encipher
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData

class TitleRepo(private val keyDataBase: KeyDataBase, private val encipher: Encipher) {


    suspend fun getAllTitle(): MutableList<TitleData> {
        val data = keyDataBase.titleDao().getAllTitle()
        encipher.unlockTitleList(data)
        return data.toMutableList()
    }

    suspend fun deleteTitle(titleData: TitleData): Int {
        encipher.lockTitle(titleData)
        return keyDataBase.titleDao().deleteByName(titleData.name)
    }

    suspend fun addTitle(titleData: TitleData): Long {
        encipher.lockTitle(titleData)
        return keyDataBase.titleDao().insertTitle(titleData)
    }

    suspend fun editTitle(titleData: TitleData): Int {
        encipher.lockTitle(titleData)
        return keyDataBase.titleDao().updateNameByOrder(titleData.name, titleData.order)
    }

    suspend fun editOrder(titleData: TitleData): Int {
        encipher.lockTitle(titleData)
        return keyDataBase.titleDao().updateOrderByName(titleData.name, titleData.order)
    }

}