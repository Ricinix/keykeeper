package com.example.keykeeper.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.keykeeper.model.repo.FragRepo
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class FragViewModel(private val fragRepo: FragRepo): ViewModel() {

    val keyList = MutableLiveData<List<KeySimplify>>()

    val wrongMsg = MutableLiveData<String>()

    fun getKeys(category: String){
        fragRepo.getKeys(category)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<KeySimplify>>{
                override fun onSuccess(t: List<KeySimplify>) {
                    keyList.value = t
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
                    getKeys(category)
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }
            })
    }

    fun deleteKey(id: Int, category: String){
        fragRepo.deleteKeys(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    getKeys(category)
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }
            })
    }
}