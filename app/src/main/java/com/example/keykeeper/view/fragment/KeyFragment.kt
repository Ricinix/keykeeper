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
import com.example.keykeeper.view.adapter.KeysRecyclerAdapter
import com.example.keykeeper.view.widget.KeyEditDialog
import com.example.keykeeper.view.widget.OnChooseLetterChangedListener
import com.example.keykeeper.viewModel.FragViewModel
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_ADD
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_DELETE
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_EDIT
import com.example.keykeeper.viewModel.FragViewModel.Companion.KEY_UNDO
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject


class KeyFragment(private val order: Int):Fragment() {
    var title: String = ""

    @Inject
    lateinit var fragViewModel: FragViewModel

//    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = KeysRecyclerAdapter()
    private lateinit var mContext: Context

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.v("LifeCycleTest", "${title}: onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        // 当选中字母导航栏其中一个字母时，滑到指定位置
        side_bar_frag_main.setListener(object :OnChooseLetterChangedListener{
            override fun onChooseLetter(s: String) {
                fragViewModel.firstLetterMap[s]?.run {
//                    Log.v("MapTest", "scroll to $this")
                    recycler_frag_main_keys.scrollToPosition(this)
                }
            }
            override fun onNoChooseLetter() {}
        })

        recycler_frag_main_keys.layoutManager = LinearLayoutManager(this.context)
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
        recycler_frag_main_keys.adapter = recyclerAdapter
    }

    private fun setObserver(){
        recyclerAdapter.copyText.observe(this, Observer {
            copyToBoard(it)
            Snackbar.make(activity?.fab_main?:recycler_frag_main_keys, "复制成功", Snackbar.LENGTH_SHORT).show()
        })

        // 获取到新的key数据
        fragViewModel.keyList.observe(this, Observer {
            Log.v("SnackBarTest", "get all keys")
            recyclerAdapter.keyList = it
            recyclerAdapter.notifyDataSetChanged()
        })
        // 如果数据流出错
        fragViewModel.wrongMsg.observe(this, Observer {wrongMsg ->
            Log.e("WrongMsg", wrongMsg)
        })
        // 增删撤改都在这里进行处理
        fragViewModel.keyChangeType.observe(this, Observer {type ->
            when (type) {
                KEY_DELETE -> Snackbar.make(activity?.fab_main?:recycler_frag_main_keys, "删除成功", Snackbar.LENGTH_LONG)
                    .setAction("撤销"){
                        fragViewModel.undoDelete()
                    }.show()
                KEY_ADD -> Snackbar.make(activity?.fab_main?:recycler_frag_main_keys, "添加成功", Snackbar.LENGTH_SHORT).show()
                KEY_UNDO -> Snackbar.make(activity?.fab_main?:recycler_frag_main_keys, "撤销成功", Snackbar.LENGTH_SHORT).show()
                KEY_EDIT -> Snackbar.make(activity?.fab_main?:recycler_frag_main_keys, "修改成功", Snackbar.LENGTH_SHORT).show()
            }
        })
        fragViewModel.title.observe(this, Observer {
            title = it
        })
    }

    fun addNewKey(){
        // 弹出自定义的Dialog
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

    // 从不可见变为可见时，自动获取keys和现在的title
    override fun onStart() {
        super.onStart()
        refreshData()
        Log.v("LifeCycleTest", "${title}: onStart")
    }

    fun refreshData(){
        fragViewModel.getTitleByOrder(order)
        fragViewModel.getKeysByOrder(order)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("LifeCycleTest", "${title}: onCreateView")
        return inflater.inflate(R.layout.fragment_main, container, false)
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