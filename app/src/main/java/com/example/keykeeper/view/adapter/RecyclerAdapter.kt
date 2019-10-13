package com.example.keykeeper.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R
import com.example.keykeeper.model.room.data.KeySimplify

class RecyclerAdapter(var keyList:List<KeySimplify>):RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val nameTextView: TextView = v.findViewById(R.id.key_name)
        val accountTextView: TextView = v.findViewById(R.id.key_account)
        val passwordTextView: TextView = v.findViewById(R.id.key_password)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.key_item, parent, false)
        view.setOnClickListener {

        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = keyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = keyList[position].name
        holder.accountTextView.text = keyList[position].account
        holder.accountTextView.text = keyList[position].password
    }
}