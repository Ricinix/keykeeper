package com.example.keykeeper.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R

class GeneralSettingRecyclerAdapter(context:Context):
    RecyclerView.Adapter<GeneralSettingRecyclerAdapter.SettingViewHolder>() {

    private val settingTitleList = context.resources.getStringArray(R.array.general_setting_items)
    private val settingDescribeList = context.resources.getStringArray(R.array.general_setting_description)
    val buttonPressId = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.setting_item, parent, false)
        return SettingViewHolder(v)
    }

    override fun getItemCount(): Int = settingTitleList.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.textTitle.text = settingTitleList[position]
        holder.textDescribe.text = settingDescribeList[position]
    }

    inner class SettingViewHolder(v: View):RecyclerView.ViewHolder(v){
        val textTitle: TextView = v.findViewById(R.id.setting_title)
        val textDescribe: TextView = v.findViewById(R.id.setting_describe)

        init {
            v.setOnClickListener {
                buttonPressId.value = adapterPosition
            }
        }
    }

    companion object{
        const val TITLE_SETTING = 0
        const val DETAIL_SETTING = 1
    }

}