package com.example.keykeeper.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.keykeeper.model.repo.TitleRepo
import com.example.keykeeper.model.room.data.TitleData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TitleViewModel(private val titleRepo: TitleRepo): ViewModel() {

    val titles = MutableLiveData<MutableList<TitleData>>()

    val wrongMsg = MutableLiveData<Int>()

    fun getAllTitle(){
        titleRepo.getAllTitle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<MutableList<TitleData>>{
                override fun onSuccess(t: MutableList<TitleData>) {
                    Log.v("DataBaseTest", "Title Setting get $t")
                    titles.value = t.also { it.add(TitleData("", t.size)) }
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun deleteTitle(titleName: String){
        titleRepo.deleteTitle(titleName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    Log.v("DataBaseTest", "succeed in updating $titleName, $t")
                    getAllTitle()
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }

    fun addTitle(titleName: String){
        titleRepo.addTitle(titleName, titles.value?.lastIndex?:0)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :SingleObserver<Long>{
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
                override fun onSuccess(t: Long) {
                    if (t == -1L){
                        wrongMsg.value = INSERT_CONFLICT
                        Log.v("DataBaseTest", "insertFail for conflict")
                    }else{
                        Log.v("DataBaseTest", "succeed in inserting $titleName, $t")
                        getAllTitle()
                    }
                }
            })
    }

    fun editTitle(titleData: TitleData){
        Log.v("DataBaseTest", "updating the $titleData")
        titleRepo.editTitle(titleData.name, titleData.order)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int>{
                override fun onSuccess(t: Int) {
                    getAllTitle()
                }
                override fun onSubscribe(d: Disposable) {}
                override fun onError(e: Throwable) {}
            })
    }


    fun updateAllTitles(titleList: List<TitleData>) {
        for (index in titleList.indices){
            titleRepo.editOrder(titleList[index].name, index)
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Int>{
                    override fun onSuccess(t: Int) {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {}
                })
        }
    }

    companion object{
        const val INSERT_CONFLICT = 0
    }

}