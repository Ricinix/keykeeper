package com.example.keykeeper.view.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keykeeper.R
import com.example.keykeeper.di.component.DaggerFragComponent
import com.example.keykeeper.di.module.FragModule
import com.example.keykeeper.model.room.data.KeySimplify
import com.example.keykeeper.view.adapter.RecyclerAdapter
import com.example.keykeeper.view.widget.KeyEditDialog
import com.example.keykeeper.viewModel.FragViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.frag_layout.*
import javax.inject.Inject

class KeyFragment(val title: String):Fragment() {

    @Inject
    lateinit var fragViewModel: FragViewModel

    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = RecyclerAdapter(listOf(KeySimplify("testName", "testAccount", "TestPwd")))
    lateinit var activityAbove: Activity

    fun addNewKey(){
        val editDialog = KeyEditDialog(this.context?:activityAbove, object :KeyEditDialog.Listener{
            override fun onConfirm(name: String, account: String, password: String, kind: String) {

            }

            override fun onCancel() {

            }

        })
        editDialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        keys_recycler.layoutManager = LinearLayoutManager(this.context)
        keys_recycler.adapter = recyclerAdapter
        fragViewModel.keyList.observe(this, Observer {
            recyclerAdapter.keyList = it
            recyclerAdapter.notifyDataSetChanged()
        })
        fragViewModel.wrongMsg.observe(this, Observer {
            Toast.makeText(this@KeyFragment.context, it, Toast.LENGTH_LONG).show()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        activityAbove = activity
    }

    override fun onResume() {
        super.onResume()
//        fragViewModel.getKeys(title)
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_layout, container, false)
    }

    private fun inject(){
        DaggerFragComponent.builder().fragModule(FragModule(this)).build().inject(this)
    }
}