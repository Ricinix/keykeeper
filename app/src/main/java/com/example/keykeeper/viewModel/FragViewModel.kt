package com.example.keykeeper.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import com.github.promeg.pinyinhelper.Pinyin
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*

class FragViewModel(private val fragRepo: FragRepo): ViewModel() {

    private var lastKey:KeyData? = null

    val firstLetterMap = mutableMapOf<String, Int>()

    val keyList = MutableLiveData<List<KeySimplify>>()

    val wrongMsg = MutableLiveData<String>()

    val keyChangeType = MutableLiveData<Int>()

    val title = MutableLiveData<String>()

    fun getTitleByOrder(order: Int){
        fragRepo.getTitleByOrder(order)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<String>{
                override fun onSuccess(t: String) {
                    title.value = t
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun getKeysByOrder(order: Int){
        fragRepo.getKeysByOrder(order)
            .map {
                it.sortedBy { key ->
                    var c =Pinyin.toPinyin(key.name[0]).toUpperCase(Locale.ROOT)
                    if (c<"A" || c >"Z"){
                        c = "a"
                    }
                    c
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<KeySimplify>>{
                override fun onSuccess(t: List<KeySimplify>) {
                    Log.v("SortTest", t.toString())
                    keyList.value = t
                    var oldFirst = ""
                    for (i in t.indices){
                        val newFirst = Pinyin.toPinyin(t[i].name[0]).toUpperCase(Locale.ROOT)
                        if (oldFirst != newFirst){
                            if (newFirst>="A" && newFirst<="Z")
                                firstLetterMap[newFirst] = i
                            else
                                firstLetterMap["#"] = i
                            oldFirst = newFirst
                        }
                    }
                    Log.v("MapTest", firstLetterMap.toString())
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }

            })
    }

    fun getKeysByCategory(category: String){
        fragRepo.getKeysByCategory(category)
            .map {
                it.sortedBy { key ->
                    var c =Pinyin.toPinyin(key.name[0]).toUpperCase(Locale.ROOT)
                    if (c<"A" || c >"Z"){
                        c = "a"
                    }
                    c
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<KeySimplify>>{
                override fun onSuccess(t: List<KeySimplify>) {
                    Log.v("SortTest", t.toString())
                    keyList.value = t
                    var oldFirst = ""
                    for (i in t.indices){
                        val newFirst = Pinyin.toPinyin(t[i].name[0]).toUpperCase(Locale.ROOT)
                        if (oldFirst != newFirst){
                            if (newFirst>="A" && newFirst<="Z")
                                firstLetterMap[newFirst] = i
                            else
                                firstLetterMap["#"] = i
                            oldFirst = newFirst
                        }
                    }
                    Log.v("MapTest", firstLetterMap.toString())
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }

            })
    }

    fun addNewData(name:String, account:String, password:String, kind: String, category: String){
        val key = KeyData(name = name, account = account,
            password = password, kind = kind, category = category)
        fragRepo.storeKeys(key)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Long>{
                override fun onSuccess(t: Long) {
                    keyChangeType.value = KEY_ADD
                    getKeysByCategory(category)
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }
            })
    }

    fun updateKey(keySimplify: KeySimplify, category: String){
        fragRepo.updateKeys(keySimplify.toKeyData(category))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    keyChangeType.value = KEY_EDIT
                    getKeysByCategory(category)
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }
            })
    }

    fun deleteKey(keySimplify: KeySimplify, category: String){
        lastKey = KeyData(name = keySimplify.name,
            account = keySimplify.account,
            password = keySimplify.password,
            kind = keySimplify.kind,
            category = category)
        fragRepo.deleteKeys(keySimplify.id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    keyChangeType.value = KEY_DELETE
                    getKeysByCategory(category)
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }
            })
    }

    fun undoDelete(){
        lastKey?.let {
            fragRepo.storeKeys(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :SingleObserver<Long>{
                    override fun onSuccess(t: Long) {
                        keyChangeType.value = KEY_UNDO
                        getKeysByCategory(it.category)
                    }
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        wrongMsg.value = e.message
                    }
                })
        }

    }

    companion object{
        const val KEY_DELETE = 0
        const val KEY_ADD = 1
        const val KEY_UNDO = 2
        const val KEY_EDIT = 3
    }
}