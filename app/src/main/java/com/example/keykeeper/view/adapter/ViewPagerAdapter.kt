package com.example.keykeeper.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.keykeeper.view.fragment.KeyFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,
                       var fragmentList: List<KeyFragment>,
                       var tabTitle: List<String>):
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitle[position]
    }

}