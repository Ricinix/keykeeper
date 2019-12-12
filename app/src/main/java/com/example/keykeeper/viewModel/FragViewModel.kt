package com.example.keykeeper.viewModel

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

    // 获取title（fragment刚创建时并不知道自己的title，而且用户修改了title后，同一个fragment的title会变，而且keys也会变）
    fun getTitleByOrder(order: Int) {
        viewModelScope.launch {
            title.value = withContext(Dispatchers.IO) {
                fragRepo.getTitleByOrder(order).name
            }
        }
    }

    // 通过tab中的序号来获取对应的keys（供fragment使用，这样fragment就不需要等拿到title才能拿keys了）
    fun getKeysByOrder(order: Int) = viewModelScope.launch {
        val t = withContext(Dispatchers.IO) {
            fragRepo.getKeysByOrder(order)
        }
        handleGetKeys(t)
    }


    // 通过类别来获取对应的所有key（这个一般是此类里面调用，因为此ViewModel不知道order）
    private fun getKeysByCategory(category: String) = viewModelScope.launch {
        val t = withContext(Dispatchers.IO){
            fragRepo.getKeysByCategory(category)
        }
        handleGetKeys(t)
    }

    // 排序加设置liveData
    private fun handleGetKeys(t : List<KeySimplify>){
        // 按拼音排序
        val sortedList = t.sortedBy { key ->
            var c = Pinyin.toPinyin(key.name[0]).toUpperCase(Locale.ROOT)
            if (c < "A" || c > "Z") {
                c = "a"
            }
            c
        }
        Log.v("SortTest", sortedList.toString())
        keyList.value = sortedList
        var oldFirst = ""
        // 记录下各首字母的第一个索引
        for (i in sortedList.indices) {
            val newFirst = Pinyin.toPinyin(sortedList[i].name[0]).toUpperCase(Locale.ROOT)
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


    // 添加新的key
    fun addNewData(keyData: KeyData) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            fragRepo.storeKeys(keyData)
        }
        keyChangeType.value = KEY_ADD
        getKeysByCategory(keyData.category)
    }


    // 更新key
    fun updateKey(keySimplify: KeySimplify, category: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            fragRepo.updateKeys(keySimplify.toKeyData(category))
        }
        keyChangeType.value = KEY_EDIT
        getKeysByCategory(category)
    }


    // 删除操作，并记录下删除了什么以供撤销
    fun deleteKey(keySimplify: KeySimplify, category: String) = viewModelScope.launch {
        lastKey = keySimplify.toKeyData(category)
        withContext(Dispatchers.IO) {
            fragRepo.deleteKeys(keySimplify)
        }
        keyChangeType.value = KEY_DELETE
        getKeysByCategory(category)
    }


    // 撤销删除操作
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