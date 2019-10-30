package com.example.keykeeper.view.adapter

import android.content.Context
import android.graphics.Color
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
import com.example.keykeeper.model.room.data.TitleData

class TitleRecyclerAdapter:RecyclerView.Adapter<TitleRecyclerAdapter.ViewHolder>() {
    private lateinit var mContext:Context
    private lateinit var dragListener: OnDragListener
    private var mPosition = 0
    val deleteTitle = MutableLiveData<String>()
    val editTitle = MutableLiveData<TitleData>()
    val addTitle = MutableLiveData<String>()
    var titles = mutableListOf<TitleData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val v =  LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = titles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == titles.lastIndex){

            holder.run {
                titleLayout.visibility = View.GONE
                addBtn.visibility = View.VISIBLE
            }
        }else{
            holder.run {
                titleLayout.visibility = View.VISIBLE
                titleText.text = titles[position].name
                addBtn.visibility = View.GONE
            }
        }
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val titleText: TextView = v.findViewById(R.id.title_text)
        private val positionBtn: ImageButton = v.findViewById(R.id.position_set_btn)
        val titleLayout: LinearLayout = v.findViewById(R.id.title_layout)
        val addBtn: ImageButton = v.findViewById(R.id.add_btn)

        init {
            addBtn.setOnClickListener {
                showEditDialog("添加中")
            }
            positionBtn.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN){
                    dragListener.onClick(this)
                }
                false
            }
            titleText.setOnLongClickListener {
                mPosition = adapterPosition
                false
            }
            titleText.setOnCreateContextMenuListener { menu, _, _ ->
                menu.run {
                    add("编辑")
                    add("删除")
                }
            }
        }
    }

    fun setDragListener(OnDragListener: OnDragListener){
        dragListener = OnDragListener
    }

    fun showDeleteDialog(){
        AlertDialog.Builder(mContext)
            .setTitle("删除")
            .setMessage("确定要删除此标题吗？")
            .setCancelable(true)
            .setPositiveButton("确定") { _, _ ->
                deleteTitle.value = titles[mPosition].name
            }
            .setNegativeButton("取消"){ _, _ ->}
            .create()
            .show()
    }

    fun editTitle(){
        showEditDialog("编辑中", mPosition)
    }

    private fun showEditDialog(name: String, i: Int = -1){
        val dialogView = View.inflate(mContext, R.layout.dialog_title, null)
        val edit: EditText = dialogView.findViewById(R.id.dialog_title)
        if (i != -1){
            edit.text = SpannableStringBuilder(titles[i].name)
        }
        AlertDialog.Builder(mContext)
            .setTitle(name)
            .setView(dialogView)
            .setPositiveButton("确定"){_, _ ->
                val titleName = edit.text.toString()
                if (i == -1){
                    addTitle.value = titleName
                }else{
                    editTitle.value = TitleData(titleName, i)
                }
            }
            .setNegativeButton("取消"){_, _ ->}
            .setCancelable(false)
            .create()
            .show()
    }

    interface OnDragListener{
        fun onClick(holder: ViewHolder)
    }
}