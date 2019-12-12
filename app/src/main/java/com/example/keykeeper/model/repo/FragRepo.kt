package com.example.keykeeper.model.repo

import com.example.keykeeper.domain.Encipher
import com.example.keykeeper.model.room.KeyDataBase
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import com.example.keykeeper.model.room.data.TitleData

class FragRepo(private val keyDataBase: KeyDataBase, private val encipher: Encipher) {

    suspend fun getTitleByOrder(order: Int): TitleData {
        val data = keyDataBase.titleDao().getTitleByOrder(order)
        encipher.unlockTitle(data)
        return data
    }

    suspend fun getKeysByOrder(order: Int): List<KeySimplify> {
        val data = keyDataBase.keyDao().getByOrder(order)
        encipher.unlockKeySimplifyList(data)
        return data
    }

    suspend fun getKeysByCategory(category: String): List<KeySimplify> {
        val data = keyDataBase.keyDao().getByKind(category)
        encipher.unlockKeySimplifyList(data)
        return data
    }

    suspend fun storeKeys(keyData: KeyData): Long {
        encipher.lockKeyData(keyData)
        return keyDataBase.keyDao().insertKeyData(keyData)
    }

    suspend fun deleteKeys(keySimplify: KeySimplify): Int {
        encipher.lockKeySimplify(keySimplify)
        return keyDataBase.keyDao().deleteById(keySimplify.id)
    }

    suspend fun updateKeys(keyData: KeyData): Int {
        encipher.lockKeyData(keyData)
        return keyDataBase.keyDao().updateKeyData(keyData)
    }
}