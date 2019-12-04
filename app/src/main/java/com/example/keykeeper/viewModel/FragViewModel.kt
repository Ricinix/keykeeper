package com.example.keykeeper.viewModel

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class FragViewModel(private val fragRepo: FragRepo) : ViewModel() {

    private var lastKey: KeyData? = null

    val firstLetterMap = mutableMapOf<String, Int>()
    val keyList = MutableLiveData<List<KeySimplify>>()
    val keyChangeType = MutableLiveData<Int>()
    val title = MutableLiveData<String>()

    fun getTitleByOrder(order: Int) {
        viewModelScope.launch {
            title.value = withContext(Dispatchers.IO) {
                fragRepo.getTitleByOrder(order)
            }
        }
    }

    fun getKeysByOrder(order: Int) = viewModelScope.launch {
        val t = withContext(Dispatchers.IO) {
            fragRepo.getKeysByOrder(order)
        }
        t.sortedBy { key ->
            var c = Pinyin.toPinyin(key.name[0]).toUpperCase(Locale.ROOT)
            if (c < "A" || c > "Z") {
                c = "a"
            }
            c
        }
        Log.v("SortTest", t.toString())
        keyList.value = t
        var oldFirst = ""
        for (i in t.indices) {
            val newFirst = Pinyin.toPinyin(t[i].name[0]).toUpperCase(Locale.ROOT)
            if (oldFirst != newFirst) {
                if (newFirst >= "A" && newFirst <= "Z")
                    firstLetterMap[newFirst] = i
                else
                    firstLetterMap["#"] = i
                oldFirst = newFirst
            }
        }
        Log.v("MapTest", firstLetterMap.toString())
    }


    private fun getKeysByCategory(category: String) = viewModelScope.launch {
        val t = fragRepo.getKeysByCategory(category)
        t.sortedBy { key ->
            var c = Pinyin.toPinyin(key.name[0]).toUpperCase(Locale.ROOT)
            if (c < "A" || c > "Z") {
                c = "a"
            }
            c
        }
        Log.v("SortTest", t.toString())
        keyList.value = t
        var oldFirst = ""
        for (i in t.indices) {
            val newFirst = Pinyin.toPinyin(t[i].name[0]).toUpperCase(Locale.ROOT)
            if (oldFirst != newFirst) {
                if (newFirst >= "A" && newFirst <= "Z")
                    firstLetterMap[newFirst] = i
                else
                    firstLetterMap["#"] = i
                oldFirst = newFirst
            }
        }
        Log.v("MapTest", firstLetterMap.toString())
    }


    fun addNewData(
        name: String,
        account: String,
        password: String,
        kind: String,
        category: String
    ) = viewModelScope.launch {
        val key = KeyData(
            name = name, account = account,
            password = password, kind = kind, category = category
        )

        withContext(Dispatchers.IO) {
            fragRepo.storeKeys(key)
        }
        keyChangeType.value = KEY_ADD
        getKeysByCategory(category)
    }


    fun updateKey(keySimplify: KeySimplify, category: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            fragRepo.updateKeys(keySimplify.toKeyData(category))
        }
        keyChangeType.value = KEY_EDIT
        getKeysByCategory(category)
    }


    fun deleteKey(keySimplify: KeySimplify, category: String) = viewModelScope.launch {
        lastKey = keySimplify.toKeyData(category)
        withContext(Dispatchers.IO) {
            fragRepo.deleteKeys(keySimplify.id)
        }
        keyChangeType.value = KEY_DELETE
        getKeysByCategory(category)
    }


    fun undoDelete() {
        lastKey?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    fragRepo.storeKeys(it)
                }
                keyChangeType.value = KEY_UNDO
                getKeysByCategory(it.category)
            }
        }

    }

    companion object {
        const val KEY_DELETE = 0
        const val KEY_ADD = 1
        const val KEY_UNDO = 2
        const val KEY_EDIT = 3
    }
}