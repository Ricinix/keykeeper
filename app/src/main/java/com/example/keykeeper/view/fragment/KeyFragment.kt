package com.example.keykeeper.view.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
import com.example.keykeeper.view.adapter.ItemBtnListener
import com.example.keykeeper.view.adapter.RecyclerAdapter
import com.example.keykeeper.view.widget.KeyEditDialog
import com.example.keykeeper.viewModel.FragViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_layout.*
import javax.inject.Inject

class KeyFragment(val title: String):Fragment() {

    @Inject
    lateinit var fragViewModel: FragViewModel

//    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = RecyclerAdapter()
    lateinit var activityAbove: Activity

    fun addNewKey(){
        val editDialog = KeyEditDialog(this.context?:activityAbove, object :KeyEditDialog.Listener{
            override fun onConfirm(name: String, account: String, password: String, kind: String) {
                fragViewModel.addNewData(name, account, password, kind, title)
            }
            override fun onCancel() {}
        })
        editDialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        keys_recycler.layoutManager = LinearLayoutManager(this.context)
        recyclerAdapter.setListener(object :ItemBtnListener{
            override fun onEdit(keySimplify: KeySimplify) {

            }

            override fun onDelete(keySimplify: KeySimplify) {
                AlertDialog.Builder(this@KeyFragment.context?:activityAbove)
                    .setTitle("删除")
                    .setMessage("确定要删除${keySimplify.name}吗？")
                    .setPositiveButton("确定") { _, _ ->
                        fragViewModel.deleteKey(keySimplify, title)
                    }.setNegativeButton("取消") { _, _ ->}.show()
            }
        })
        keys_recycler.adapter = recyclerAdapter
        fragViewModel.keyList.observe(this, Observer {
            recyclerAdapter.keyList = it
            recyclerAdapter.notifyDataSetChanged()
        })
        fragViewModel.wrongMsg.observe(this, Observer {wrongMsg ->
            Toast.makeText(this@KeyFragment.context, wrongMsg, Toast.LENGTH_LONG).show()
            Log.e("WrongMsg", wrongMsg)
        })
        fragViewModel.numChanged.observe(this, Observer {num ->
            when {
                num < 0 -> Snackbar.make(activityAbove.view_pager, "删除成功", Snackbar.LENGTH_LONG)
                    .setAction("撤销"){
                        fragViewModel.undoDelete()
                    }.show()
                num == 1L -> Snackbar.make(activityAbove.view_pager, "添加成功", Snackbar.LENGTH_SHORT).show()
                num == 2L -> Snackbar.make(activityAbove.view_pager, "撤销成功", Snackbar.LENGTH_SHORT).show()
                else -> Snackbar.make(activityAbove.view_pager, "修改成功", Snackbar.LENGTH_SHORT).show()
            }
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
        fragViewModel.getKeys(title)
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