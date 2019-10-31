package com.example.keykeeper.view.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keykeeper.R
import com.example.keykeeper.di.component.DaggerFragComponent
import com.example.keykeeper.di.module.FragModule
import com.example.keykeeper.model.room.data.KeySimplify
import com.example.keykeeper.view.MyApplication
import com.example.keykeeper.view.adapter.ItemBtnListener
import com.example.keykeeper.view.adapter.RecyclerAdapter
import com.example.keykeeper.view.widget.KeyEditDialog
import com.example.keykeeper.view.widget.OnChooseLetterChangedListener
import com.example.keykeeper.viewModel.FragViewModel
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_ADD
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_DELETE
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_EDIT
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_UNDO
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_layout.*
import javax.inject.Inject


class KeyFragment(val order: Int):Fragment() {
    var title: String = ""

    @Inject
    lateinit var fragViewModel: FragViewModel

//    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = RecyclerAdapter()
    private lateinit var mContext: Context

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.v("LifeCycleTest", "${title}: onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        side_bar.setListener(object :OnChooseLetterChangedListener{
            override fun onChooseLetter(s: String) {
                fragViewModel.firstLetterMap[s]?.run {
//                    Log.v("MapTest", "scroll to $this")
                    keys_recycler.scrollToPosition(this)
                }
            }
            override fun onNoChooseLetter() {}
        })

        keys_recycler.layoutManager = LinearLayoutManager(this.context)
        recyclerAdapter.setListener(object :ItemBtnListener{
            override fun onEdit(keySimplify: KeySimplify) {
                editKey(keySimplify)
            }

            override fun onDelete(keySimplify: KeySimplify) {
                AlertDialog.Builder(mContext)
                    .setTitle("删除")
                    .setMessage("确定要删除${keySimplify.name}吗？")
                    .setPositiveButton("确定") { _, _ ->
                        fragViewModel.deleteKey(keySimplify, title)
                    }.setNegativeButton("取消") { _, _ ->}.show()
            }
        })
        keys_recycler.adapter = recyclerAdapter
    }

    private fun setObserver(){
        recyclerAdapter.copyText.observe(this, Observer {
            copyToBoard(it)
            Snackbar.make(activity?.fab?:keys_recycler, "复制成功", Snackbar.LENGTH_SHORT).show()
        })

        fragViewModel.keyList.observe(this, Observer {
            Log.v("SnackBarTest", "get all keys")
            recyclerAdapter.keyList = it
            recyclerAdapter.notifyDataSetChanged()
        })
        fragViewModel.wrongMsg.observe(this, Observer {wrongMsg ->
            Log.e("WrongMsg", wrongMsg)
        })
        fragViewModel.keyChangeType.observe(this, Observer {type ->
            when (type) {
                KEY_DELETE -> Snackbar.make(activity?.fab?:keys_recycler, "删除成功", Snackbar.LENGTH_LONG)
                    .setAction("撤销"){
                        fragViewModel.undoDelete()
                    }.show()
                KEY_ADD -> Snackbar.make(activity?.fab?:keys_recycler, "添加成功", Snackbar.LENGTH_SHORT).show()
                KEY_UNDO -> Snackbar.make(activity?.fab?:keys_recycler, "撤销成功", Snackbar.LENGTH_SHORT).show()
                KEY_EDIT -> Snackbar.make(activity?.fab?:keys_recycler, "修改成功", Snackbar.LENGTH_SHORT).show()
            }
        })
        fragViewModel.title.observe(this, Observer {
            title = it
        })
    }

    fun addNewKey(){
        val editDialog = KeyEditDialog(mContext, object :KeyEditDialog.Listener{
            override fun onConfirm(name: String, account: String, password: String, kind: String) {
                fragViewModel.addNewData(name, account, password, kind, title)
            }
            override fun onCancel() {}
        })
        editDialog.show()
    }

    fun editKey(keySimplify: KeySimplify){
        val editDialog = KeyEditDialog(mContext, object :KeyEditDialog.Listener{
            override fun onConfirm(name: String, account: String, password: String, kind: String) {
                keySimplify.name = name
                keySimplify.account = account
                keySimplify.password = password
                keySimplify.kind = kind
                fragViewModel.updateKey(keySimplify, title)
            }
            override fun onCancel() {}
        })
        editDialog.show()
        editDialog.setMessage(keySimplify.name, keySimplify.account, keySimplify.password, keySimplify.kind)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("LifeCycleTest", "${title}: onCreate")
        inject()
        setObserver()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        Log.v("LifeCycleTest", "${title}: onResume")
    }

    override fun onStart() {
        super.onStart()
        fragViewModel.getTitleByOrder(order)
        fragViewModel.getKeysByOrder(order)
        Log.v("LifeCycleTest", "${title}: onStart")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("LifeCycleTest", "${title}: onCreateView")
        return inflater.inflate(R.layout.frag_layout, container, false)
    }

    private fun inject(){
        DaggerFragComponent.builder()
            .baseComponent((activity?.application as MyApplication).getBaseComponent())
            .fragModule(FragModule(this))
            .build()
            .inject(this)
    }

    private fun copyToBoard(s:String){
        val cm = this.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", s)
        cm.primaryClip = mClipData
    }

}