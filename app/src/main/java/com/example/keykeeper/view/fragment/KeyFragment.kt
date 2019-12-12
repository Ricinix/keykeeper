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
import androidx.recyclerview.widget.RecyclerView
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


class KeyFragment(private val order: Int) : Fragment() {
    var title: String = ""
    private var mNeedScroll = false
    private var nextPosition = 0

    @Inject
    lateinit var fragViewModel: FragViewModel

    //    private val compositeDisposable = CompositeDisposable()
    private val recyclerAdapter = KeysRecyclerAdapter()
    private lateinit var mContext: Context

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.v("LifeCycleTest", "${title}: onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        // 当选中字母导航栏其中一个字母时，滑到指定位置
        side_bar_frag_main.setListener(object : OnChooseLetterChangedListener {
            override fun onChooseLetter(s: String) {
                fragViewModel.firstLetterMap[s]?.run {
                    recycler_frag_main_keys.smoothScrollToTop(this)
                }
            }

            override fun onNoChooseLetter() {}
        })

        recycler_frag_main_keys.layoutManager = LinearLayoutManager(this.context)
        recyclerAdapter.setListener(object : ItemBtnListener {
            override fun onEdit(keySimplify: KeySimplify) {
                editKey(keySimplify)
            }

            override fun onDelete(keySimplify: KeySimplify) {
                AlertDialog.Builder(mContext)
                    .setTitle("删除")
                    .setMessage("确定要删除${keySimplify.name}吗？")
                    .setPositiveButton("确定") { _, _ ->
                        fragViewModel.deleteKey(keySimplify, title)
                    }.setNegativeButton("取消") { _, _ -> }.show()
            }
        })
        recycler_frag_main_keys.adapter = recyclerAdapter
        recycler_frag_main_keys.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (mNeedScroll && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mNeedScroll = false
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val n: Int = nextPosition - linearLayoutManager.findFirstVisibleItemPosition()
                    if (n >= 0 && n < recyclerView.childCount) {
                        //获取要置顶的项顶部距离RecyclerView顶部的距离
                        val top: Int = recyclerView.getChildAt(n).top
                        //进行第二次滚动（最后的距离）
                        recyclerView.smoothScrollBy(0, top)
                    }
                }
            }
        })
    }

    private fun RecyclerView.smoothScrollToTop(position: Int) {
        val linearLayoutManager = layoutManager as LinearLayoutManager
        //获取当前RecycleView屏幕可见的第一项和最后一项的Position
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        when {
            position < firstVisibleItemPosition -> {
                //要置顶的项在当前显示的第一项之前
                smoothScrollToPosition(position)
            }
            position < lastVisibleItemPosition -> {
                //要置顶的项已经在屏幕上显示，计算它离屏幕原点的距离
                val top: Int = getChildAt(position - firstVisibleItemPosition).top
                smoothScrollBy(0, top)
            }
            else -> {
                //要置顶的项在当前显示的第一项之后
                mNeedScroll = true
                nextPosition = position
                smoothScrollToPosition(position)
            }
        }
    }

    private fun setObserver() {
        recyclerAdapter.copyText.observe(this, Observer {
            copyToBoard(it)
            Snackbar.make(
                activity?.fab_main ?: recycler_frag_main_keys,
                "复制成功",
                Snackbar.LENGTH_SHORT
            ).show()
        })

        // 获取到新的key数据
        fragViewModel.keyList.observe(this, Observer {
            Log.v("SnackBarTest", "get all keys")
            recyclerAdapter.keyList = it
            recyclerAdapter.notifyDataSetChanged()
        })
        // 增删撤改都在这里进行处理
        fragViewModel.keyChangeType.observe(this, Observer { type ->
            when (type) {
                KEY_DELETE -> Snackbar.make(
                    activity?.fab_main ?: recycler_frag_main_keys,
                    "删除成功",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("撤销") {
                        fragViewModel.undoDelete()
                    }.show()
                KEY_ADD -> Snackbar.make(
                    activity?.fab_main ?: recycler_frag_main_keys,
                    "添加成功",
                    Snackbar.LENGTH_SHORT
                ).show()
                KEY_UNDO -> Snackbar.make(
                    activity?.fab_main ?: recycler_frag_main_keys,
                    "撤销成功",
                    Snackbar.LENGTH_SHORT
                ).show()
                KEY_EDIT -> Snackbar.make(
                    activity?.fab_main ?: recycler_frag_main_keys,
                    "修改成功",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
        fragViewModel.title.observe(this, Observer {
            title = it
        })
    }

    fun addNewKey() {
        // 弹出自定义的Dialog
        val editDialog = KeyEditDialog(mContext, object : KeyEditDialog.Listener {
            override fun onConfirm(keySimplify: KeySimplify) {
                fragViewModel.addNewData(keySimplify.toKeyData(title))
            }

            override fun onCancel() {}
        })
        editDialog.show()
    }

    // 编辑key
    fun editKey(keySimplify: KeySimplify) {
        val editDialog = KeyEditDialog(mContext, object : KeyEditDialog.Listener {
            override fun onConfirm(keySimplify: KeySimplify) {
                fragViewModel.updateKey(keySimplify, title)
            }

            override fun onCancel() {}
        })
        editDialog.show()
        editDialog.setMessage(keySimplify)
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

    private fun refreshData() {
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

    private fun inject() {
        DaggerFragComponent.builder()
            .baseComponent((activity?.application as MyApplication).getBaseComponent())
            .fragModule(FragModule(this))
            .build()
            .inject(this)
    }

    // 复制到剪切板
    private fun copyToBoard(s: String) {
        val cm = this.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Label", s)
        cm.primaryClip = mClipData
    }

}