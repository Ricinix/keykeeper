package com.example.keykeeper.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R
import com.example.keykeeper.di.component.DaggerTitleComponent
import com.example.keykeeper.di.module.TitleModule
import com.example.keykeeper.view.MyApplication
import com.example.keykeeper.view.adapter.TitleRecyclerAdapter
import com.example.keykeeper.viewModel.TitleViewModel
import com.example.keykeeper.viewModel.TitleViewModel.Companion.INSERT_CONFLICT
import kotlinx.android.synthetic.main.fragment_title_setting.*
import javax.inject.Inject


class TitleSettingFragment : Fragment() {
    private lateinit var mContext: Context
    @Inject
    lateinit var viewModel: TitleViewModel

    private val adapter = TitleRecyclerAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach ")
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler_frag_title_setting.layoutManager = LinearLayoutManager(this.context)
        recycler_frag_title_setting.adapter = adapter
        setTouchHelper(adapter)
    }

    private fun setObserver() {
        // RecyclerView适配器的观察
        adapter.editTitle.observe(this, Observer {
            viewModel.editTitle(it)
        })
        adapter.deleteTitle.observe(this, Observer {
            viewModel.deleteTitle(it)
        })
        adapter.addTitle.observe(this, Observer {
            viewModel.addTitle(it)
        })

        // ViewModel的观察
        viewModel.titles.observe(this, Observer {
            adapter.titles = it
            Log.v("SettingTest", "adapter's titles: ${adapter.titles}")
            adapter.notifyDataSetChanged()
        })
        viewModel.wrongMsg.observe(this, Observer {
            when (it) {
                // 插入冲突（同名）
                INSERT_CONFLICT -> showConflictMsg()
            }
        })

    }

    /**
     * 拖动更改位置的主要实现部分
     */
    private fun setTouchHelper(adapter: TitleRecyclerAdapter) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            // 设置为按下就能立马拖动，不需要长按
            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            // 只关注上下滑动
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlag, 0)
            }

            // 若选择了某个item，就更改其背景
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder?.itemView?.background =
                        resources.getDrawable(R.drawable.bg_title_item)
                }
            }

            // 松开item时，将背景设置回去
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                // 存储更改后的列表
                viewModel.updateAllTitles(adapter.titles)
            }

            // 这里是同步list中的位置和显示的位置
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val oldPosition = viewHolder.adapterPosition
                val newPosition = target.adapterPosition
                // 最后一个不移动（因为是添加键）
                if (newPosition == adapter.titles.lastIndex)
                    return false
                if (oldPosition < newPosition) {
                    // 冒泡式下沉
                    for (i in oldPosition until newPosition) {
                        swapTitle(i, i + 1)
                    }
                } else {
                    // 冒泡式上升
                    for (i in oldPosition downTo newPosition + 1) {
                        swapTitle(i, i - 1)
                    }
                }
                // 告诉adapter这两个交换了位置
                adapter.notifyItemMoved(oldPosition, newPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        adapter.dragListener = {
            // 设置当按下指定位置时才触发拖动
            itemTouchHelper.startDrag(it)
        }
        // 绑定RecyclerViw
        itemTouchHelper.attachToRecyclerView(recycler_frag_title_setting)
    }

    /**
     * 交换两个title的位置
     */
    private fun swapTitle(oldPosition: Int, newPosition: Int) {
        val temp = adapter.titles[oldPosition]
        adapter.titles[oldPosition] = adapter.titles[newPosition]
        adapter.titles[newPosition] = temp
    }

    private fun showConflictMsg() {
        AlertDialog.Builder(mContext)
            .setTitle("失败")
            .setMessage("已存在同名的标签")
            .setPositiveButton("了解") { _, _ -> }
            .setCancelable(true)
            .create()
            .show()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            TitleRecyclerAdapter.MENU_ITEM_EDIT -> adapter.editTitle()
            TitleRecyclerAdapter.MENU_ITEM_DELETE -> adapter.showDeleteDialog()
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllTitle()
    }

    private fun inject() {
        DaggerTitleComponent.builder()
            .baseComponent((activity?.application as MyApplication).getBaseComponent())
            .titleModule(TitleModule(this))
            .build()
            .inject(this)
    }

    companion object{
        const val TAG = "titleTest"
    }
}
