package com.example.keykeeper.view.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R
import com.example.keykeeper.model.room.data.KeySimplify


class KeysRecyclerAdapter : RecyclerView.Adapter<KeysRecyclerAdapter.ViewHolder>() {
    var keyList: List<KeySimplify> = listOf()
    // 因为需要snackBar，所以在外面的fragment处理
    val copyText = MutableLiveData<String>()
    lateinit var itemBtnListener: ItemBtnListener

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nameTextView: TextView = v.findViewById(R.id.name_key)
        val accountTextView: TextView = v.findViewById(R.id.account_key)
        val passwordTextView: TextView = v.findViewById(R.id.password_key)
        private val editBtn: TextView = v.findViewById(R.id.btn_key_edit)
        private val deleteBtn: TextView = v.findViewById(R.id.btn_key_delete)
        private val copyAccountBtn: ImageButton = v.findViewById(R.id.btn_key_account_copy)
        private val copyPwdBtn: ImageButton = v.findViewById(R.id.btn_key_pwd_copy)
        private val hidePwd: Button = v.findViewById(R.id.btn_key_hide_pwd)

        init {
            editBtn.setOnClickListener {
                // 让外部弹出Dialog
                itemBtnListener.onEdit(keyList[adapterPosition])
            }
            deleteBtn.setOnClickListener {
                // 让外部弹出Dialog
                itemBtnListener.onDelete(keyList[adapterPosition])
            }
            copyAccountBtn.setOnClickListener {
                copyText.value = keyList[adapterPosition].account
            }
            copyPwdBtn.setOnClickListener {
                copyText.value = keyList[adapterPosition].password
            }

            hidePwd.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    // 按下就显示密码
                    MotionEvent.ACTION_DOWN ->
                        passwordTextView.text = keyList[adapterPosition].password
                    // 松开就隐藏密码
                    MotionEvent.ACTION_UP -> {
                        passwordTextView.text = "******"
                    }
                }
                view.performClick()
            }
        }
    }

    fun setListener(itemBtnListener: ItemBtnListener) {
        this.itemBtnListener = itemBtnListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = keyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = keyList[position].name
        holder.accountTextView.text = keyList[position].account
        holder.passwordTextView.text = "******"
    }

}

interface ItemBtnListener {
    fun onEdit(keySimplify: KeySimplify)
    fun onDelete(keySimplify: KeySimplify)
}