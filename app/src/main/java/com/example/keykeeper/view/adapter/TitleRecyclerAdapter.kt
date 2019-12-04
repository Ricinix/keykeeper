package com.example.keykeeper.view.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R
import com.example.keykeeper.domain.SingleLiveEvent
import com.example.keykeeper.model.room.data.TitleData

class TitleRecyclerAdapter : RecyclerView.Adapter<TitleRecyclerAdapter.ViewHolder>() {
    // 由于Dialog需要使用context...
    private lateinit var mContext: Context
    var dragListener: (holder: ViewHolder) -> Unit = {}
    private var mPosition = 0
    val deleteTitle = MutableLiveData<String>()
    val editTitle = SingleLiveEvent<TitleData>()
    val addTitle = SingleLiveEvent<String>()
    var titles = mutableListOf<TitleData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 从viewGroup中获取context
        mContext = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_title, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = titles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 如果是最后一个item就换成添加键
        if (position == titles.lastIndex) {
            holder.run {
                titleLayout.visibility = View.GONE
                addBtn.visibility = View.VISIBLE
            }
        } else {
            holder.run {
                titleLayout.visibility = View.VISIBLE
                titleText.text = titles[position].name
                addBtn.visibility = View.GONE
            }
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleText: TextView = v.findViewById(R.id.text_item_title)
        private val positionBtn: ImageButton = v.findViewById(R.id.btn_item_setting_position_set)
        val titleLayout: LinearLayout = v.findViewById(R.id.linearLayout_item_title)
        val addBtn: ImageButton = v.findViewById(R.id.btn_item_title_add)

        init {
            addBtn.setOnClickListener {
                showEditDialog("添加中")
            }
            // 若按住了调整位置的按键，则可以拖动来修改位置
            positionBtn.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragListener(this)
                }
                false
            }
            // 由于是长按来弹出菜单，所以需要在此监听长按，并记录下位置
            titleText.setOnLongClickListener {
                mPosition = adapterPosition
                false
            }
            // 按住来修改标题内容或者删除
            titleText.setOnCreateContextMenuListener { menu, _, _ ->
                menu.run {
                    add(MENU_ITEM_EDIT)
                    add(MENU_ITEM_DELETE)
                }
            }
        }
    }

    fun showDeleteDialog() {
        AlertDialog.Builder(mContext)
            .setTitle("删除")
            .setMessage("确定要删除此标题吗？")
            .setCancelable(true)
            .setPositiveButton("确定") { _, _ ->
                deleteTitle.value = titles[mPosition].name
            }
            .setNegativeButton("取消") { _, _ -> }
            .create()
            .show()
    }

    fun editTitle() {
        showEditDialog("编辑中", mPosition)
    }

    private fun showEditDialog(name: String, i: Int = -1) {
        // 由于原来的标题太小了，所以自己设置了一个，并动态加载进来
        val dialogView = View.inflate(mContext, R.layout.dialog_key_title, null)
        val edit: EditText = dialogView.findViewById(R.id.title_dialog_key_title)
        if (i != -1) {
            edit.text = SpannableStringBuilder(titles[i].name)
        }
        AlertDialog.Builder(mContext)
            .setTitle(name)
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                val titleName = edit.text.toString()
                if (i == -1) {
                    // 说明是新添加
                    addTitle.setValue(titleName)
                } else {
                    // 修改旧的值
                    editTitle.setValue(TitleData(titleName, i))
                }
            }
            .setNegativeButton("取消") { _, _ -> }
            .setCancelable(false)
            .create()
            .show()
    }

    companion object {
        const val MENU_ITEM_EDIT = "编辑"
        const val MENU_ITEM_DELETE = "删除"
    }

}