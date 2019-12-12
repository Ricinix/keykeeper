package com.example.keykeeper.domain

import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import com.example.keykeeper.model.room.data.TitleData

class Encipher(private val corePassword: String) {

    private fun lock(input: String): String {
        return input
    }

    private fun unlock(input: String): String {
        return input
    }

    // 锁上单个KeyData
    fun lockKeyData(keyData: KeyData) {
        lock(keyData.name)
        lock(keyData.account)
        lock(keyData.password)
        lock(keyData.category)
        lock(keyData.kind)
    }

    // 锁上单个KeySimplify
    fun lockKeySimplify(keySimplify: KeySimplify) {
        lock(keySimplify.name)
        lock(keySimplify.account)
        lock(keySimplify.password)
        lock(keySimplify.kind)
    }

    // 锁上单个TitleData
    fun lockTitle(titleData: TitleData) {
        lock(titleData.name)
    }

    // 锁上一组TitleData
    fun lockTitleList(list: List<TitleData>) {
        list.forEach {
            lock(it.name)
        }
    }

    // 解锁一组TitleData
    fun unlockTitleList(list: List<TitleData>) {
        list.forEach {
            unlock(it.name)
        }
    }

    // 解锁单个TitleData
    fun unlockTitle(titleData: TitleData) {
        unlock(titleData.name)
    }

    // 解锁一组KeySimplify
    fun unlockKeySimplifyList(list: List<KeySimplify>) {
        list.forEach {
            unlock(it.name)
            unlock(it.account)
            unlock(it.password)
            unlock(it.kind)
        }
    }
}