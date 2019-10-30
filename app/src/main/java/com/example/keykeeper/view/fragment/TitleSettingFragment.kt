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
    private lateinit var mContext:Context
    @Inject
    lateinit var viewModel: TitleViewModel

    private val adapter = TitleRecyclerAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
        mContext = context
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
        title_recycler.layoutManager = LinearLayoutManager(this.context)
        title_recycler.adapter = adapter
        setTouchHelper(adapter)
    }

    private fun setObserver(){
        adapter.editTitle.observe(this, Observer {
            viewModel.editTitle(it)
        })
        adapter.deleteTitle.observe(this, Observer {
            viewModel.deleteTitle(it)
        })
        adapter.addTitle.observe(this, Observer {
            viewModel.addTitle(it)
        })

        viewModel.titles.observe(this, Observer {
            adapter.titles = it
            Log.v("SettingTest", "adapter's titles: ${adapter.titles}")
            adapter.notifyDataSetChanged()
        })
        viewModel.wrongMsg.observe(this, Observer {
            when (it){
                INSERT_CONFLICT -> showConflictMsg()
            }
        })

    }

    private fun setTouchHelper(adapter: TitleRecyclerAdapter){
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback(){

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlag, 0)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){
                    viewHolder?.itemView?.background = resources.getDrawable(R.drawable.title_item_bg)
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                viewModel.updateAllTitles(adapter.titles)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val oldPosition = viewHolder.adapterPosition
                val newPosition = target.adapterPosition
                if (newPosition == adapter.titles.lastIndex)
                    return false
                if (oldPosition < newPosition) {
                    for (i in oldPosition until newPosition) {
                        swapTitle(i, i + 1)
                    }
                } else {
                    for (i in oldPosition downTo newPosition + 1) {
                        swapTitle(i, i - 1)
                    }
                }
                adapter.notifyItemMoved(oldPosition, newPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        adapter.setDragListener(object : TitleRecyclerAdapter.OnDragListener{
            override fun onClick(holder: TitleRecyclerAdapter.ViewHolder) {
                itemTouchHelper.startDrag(holder)
            }
        })
        itemTouchHelper.attachToRecyclerView(title_recycler)
    }

    private fun swapTitle(oldPosition: Int, newPosition: Int){
        val temp = adapter.titles[oldPosition]
        adapter.titles[oldPosition] = adapter.titles[newPosition]
        adapter.titles[newPosition] = temp
    }

    private fun showConflictMsg(){
        AlertDialog.Builder(mContext)
            .setTitle("失败")
            .setMessage("已存在同名的标签")
            .setPositiveButton("了解"){_, _ ->}
            .setCancelable(true)
            .create()
            .show()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.title){
            "编辑"-> adapter.editTitle()
            "删除" -> adapter.showDeleteDialog()
        }
        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllTitle()
    }

    private fun inject(){
        DaggerTitleComponent.builder()
            .baseComponent((activity?.application as MyApplication).getBaseComponent())
            .titleModule(TitleModule(this))
            .build()
            .inject(this)
    }
}
