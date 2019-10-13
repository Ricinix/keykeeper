package com.example.keykeeper.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R
import com.example.keykeeper.model.room.data.KeyData
import com.example.keykeeper.model.room.data.KeySimplify

class RecyclerAdapter :RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
    var keyList:List<KeySimplify> = listOf()
    lateinit var itemBtnListener: ItemBtnListener

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val nameTextView: TextView = v.findViewById(R.id.key_name)
        val accountTextView: TextView = v.findViewById(R.id.key_account)
        val passwordTextView: TextView = v.findViewById(R.id.key_password)
        private val editBtn: TextView = v.findViewById(R.id.key_edit_btn)
        private val deleteBtn: TextView = v.findViewById(R.id.key_delete_btn)
        init {
            editBtn.setOnClickListener {
                itemBtnListener.onEdit(keyList[adapterPosition])
            }
            deleteBtn.setOnClickListener {
                itemBtnListener.onDelete(keyList[adapterPosition])
            }
        }
    }

    fun setListener(itemBtnListener: ItemBtnListener){
        this.itemBtnListener = itemBtnListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.key_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = keyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = keyList[position].name
        holder.accountTextView.text = keyList[position].account
        holder.passwordTextView.text = keyList[position].password
    }
}

interface ItemBtnListener{
    fun onEdit(keySimplify: KeySimplify)
    fun onDelete(keySimplify: KeySimplify)
}