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

    fun getKeys(kind: String){
        fragRepo.getKeys(kind)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<List<KeySimplify>>{
                override fun onSuccess(t: List<KeySimplify>) {
                    keyList.value = t
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    wrongMsg.value = e.message
                }

            })
    }
}