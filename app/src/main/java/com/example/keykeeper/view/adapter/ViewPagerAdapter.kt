package com.example.keykeeper.view.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.keykeeper.view.fragment.KeyFragment

/**
 * ViewPager的适配器
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val fragmentList = mutableListOf<KeyFragment>()
    var titleList = listOf<String>()

    override fun getItem(position: Int): Fragment {
        Log.v("LifeCycleTest", "viewPager present ${fragmentList[position].title}")
        return fragmentList[position]
    }

    override fun getCount() = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? = titleList[position]

}