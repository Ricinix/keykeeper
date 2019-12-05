package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData

class TitleRepo(private val keyDataBase: KeyDataBase) {


    suspend fun getAllTitle(): MutableList<TitleData> {
        return keyDataBase.titleDao().getAllTitle().toMutableList()
    }

    suspend fun deleteTitle(titleData: TitleData): Int {
        return keyDataBase.titleDao().deleteByName(titleData.name)
    }

    suspend fun addTitle(titleData: TitleData): Long {
        return keyDataBase.titleDao().insertTitle(titleData)
    }

    suspend fun editTitle(titleData: TitleData): Int {
        return keyDataBase.titleDao().updateNameByOrder(titleData.name, titleData.order)
    }

    suspend fun editOrder(titleData: TitleData): Int {
        return keyDataBase.titleDao().updateOrderByName(titleData.name, titleData.order)
    }

}