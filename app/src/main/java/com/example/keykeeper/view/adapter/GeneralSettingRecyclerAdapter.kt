package com.example.keykeeper.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.keykeeper.R

class GeneralSettingRecyclerAdapter(context: Context) :
    RecyclerView.Adapter<GeneralSettingRecyclerAdapter.SettingViewHolder>() {

    // 从array静态资源中获取设置的目录及细节
    private val settingTitleList = context.resources.getStringArray(R.array.general_setting_items)
    private val settingDescribeList =
        context.resources.getStringArray(R.array.general_setting_description)
    val buttonPressId = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingViewHolder(v)
    }

    override fun getItemCount(): Int = settingTitleList.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.textTitle.text = settingTitleList[position]
        holder.textDescribe.text = settingDescribeList[position]
    }

    inner class SettingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textTitle: TextView = v.findViewById(R.id.title_item_setting)
        val textDescribe: TextView = v.findViewById(R.id.details_item_setting_describe)

        init {
            v.setOnClickListener {
                // 监听时选择了哪个二级设置页面
                buttonPressId.value = adapterPosition
            }
        }
    }

    companion object {
        const val TITLE_SETTING = 0
        const val DETAIL_SETTING = 1
    }

}