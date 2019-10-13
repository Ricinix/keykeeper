package com.example.keykeeper.model.repo

import android.content.SharedPreferences

class MainRepo(
    private val settingSP:SharedPreferences,
    private val titleSP: SharedPreferences
){
    private val titleEditor = titleSP.edit()

    fun getTitle(): List<String>{
        val titleNum = titleSP.getInt("num", 0)
        return if (titleNum == 0){
            titleEditor.putString("title1", "工作")
            titleEditor.putString("title2", "学习")
            titleEditor.apply()
            listOf("工作", "学习")
        }else{
            val titleList = arrayListOf<String>()
            for (index in 1..titleNum){
                titleList.add(titleSP.getString("title$index", "未知")?:"未知")
            }
            titleList
        }
    }

}