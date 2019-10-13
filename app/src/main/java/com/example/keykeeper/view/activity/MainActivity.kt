package com.example.keykeeper.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.keykeeper.R
import com.example.keykeeper.di.component.DaggerMainComponent
import com.example.keykeeper.di.module.MainModule
import com.example.keykeeper.view.adapter.ViewPagerAdapter
import com.example.keykeeper.view.fragment.KeyFragment
import com.example.keykeeper.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel:MainViewModel

    private val mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager,  listOf<KeyFragment>(), listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tool_bar)
        inject()

        setViewPagerListener()
        mainViewModel.getTitle()
        setBtnListener()
    }

    private fun setBtnListener(){
        fab.setOnClickListener {
            val itemFrag= mViewPagerAdapter.instantiateItem(view_pager, view_pager.currentItem) as KeyFragment
            itemFrag.addNewKey()
            Log.v("BtnTest", itemFrag.title)
        }
    }

    private fun setViewPagerListener(){
        view_pager.adapter = mViewPagerAdapter
        tab_layout.setupWithViewPager(view_pager)
        mainViewModel.tabTitle.observe(this, Observer {newTitles->
            val temp = ArrayList<KeyFragment>()
            for (title in newTitles){
                val index = mViewPagerAdapter.fragmentList.binarySearchBy(title){ keyFragment->
                    keyFragment.title
                }
                if (-1 != index){
                    temp.add(mViewPagerAdapter.fragmentList[index])
                }else{
                    temp.add(KeyFragment(title))
                }
            }
            Log.v("TabTitleTest", temp.toString())
            mViewPagerAdapter.let { adapter->
                adapter.fragmentList = temp
                adapter.tabTitle = newTitles
                adapter.notifyDataSetChanged()
            }

        })
    }

    private fun inject(){
        DaggerMainComponent.builder().mainModule(MainModule(this)).build().inject(this)
    }
}
