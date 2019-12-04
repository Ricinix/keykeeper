package com.example.keykeeper.model.repo

import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify

class FragRepo(private val keyDataBase: KeyDataBase) {

    suspend fun getTitleByOrder(order: Int): String {
        return keyDataBase.titleDao().getTitleByOrder(order).name
    }

    suspend fun getKeysByOrder(order: Int): List<KeySimplify> {
        return keyDataBase.keyDao().getByOrder(order)
    }

    suspend fun getKeysByCategory(category: String): List<KeySimplify> {
        return keyDataBase.keyDao().getByKind(category)
    }

    suspend fun storeKeys(keyData: KeyData): Long {
        return keyDataBase.keyDao().insertKeyData(keyData)
    }

    suspend fun deleteKeys(id: Int): Int {
        return keyDataBase.keyDao().deleteById(id)
    }

    suspend fun updateKeys(keysData: KeyData): Int {
        return keyDataBase.keyDao().updateKeyData(keysData)
    }
}