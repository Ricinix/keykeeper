package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.TitleData

class TitleRepo(private val keyDataBase: KeyDataBase) {


    suspend fun getAllTitle(): MutableList<TitleData> {
        return keyDataBase.titleDao().getAllTitle().toMutableList()
    }

    suspend fun deleteTitle(name: String): Int {
        return keyDataBase.titleDao().deleteByName(name)
    }

    suspend fun addTitle(title: String, order: Int): Long {
        return keyDataBase.titleDao().insertTitle(TitleData(title, order))
    }

    suspend fun editTitle(newName: String, order: Int): Int {
        return keyDataBase.titleDao().updateNameByOrder(newName, order)
    }

    suspend fun editOrder(name: String, order: Int): Int {
        return keyDataBase.titleDao().updateOrderByName(name, order)
    }

}