package com.example.keykeeper.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.example.keykeeper.R
import com.example.keykeeper.di.component.DaggerMainComponent
import com.example.keykeeper.di.module.MainModule
import com.example.keykeeper.view.MyApplication
import com.example.keykeeper.view.adapter.ViewPagerAdapter
import com.example.keykeeper.view.fragment.KeyFragment
import com.example.keykeeper.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import kotlin.math.min

class MainActivity : BaseActivity() {

    @Inject
    lateinit var mainViewModel:MainViewModel

    private val mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)
        setMyContentView(main_layout, cover_layout, number_panel_layout)
        if (password.isEmpty()){
            IntroActivity.startThisActivity(this)
            finish()
        }
        if (intent.getBooleanExtra("need_check_finger_print", true)){
            checkFingerPrint()
        }
        setSupportActionBar(tool_bar)
        inject()

        setViewPagerListener()
        setBtnListener()
        setObserver()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.getTitle()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_setting ->{
                setCheckCodeToActivity()
                SettingsActivity.startThisActivity(this)
            }
        }
        return true
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
    }

    private fun setObserver(){
        mainViewModel.tabTitle.observe(this, Observer {newTitles->
            val minLastIndex = min(mViewPagerAdapter.fragmentList.lastIndex, newTitles.lastIndex)
            // some Fragments need removing
            if (minLastIndex < mViewPagerAdapter.fragmentList.lastIndex){
                for (index in minLastIndex+1..mViewPagerAdapter.fragmentList.lastIndex){
                    mViewPagerAdapter.fragmentList.removeAt(index)
                }
            }
            // some Fragments need adding
            else if (minLastIndex < newTitles.lastIndex){
                for (index in minLastIndex+1..newTitles.lastIndex){
                    mViewPagerAdapter.fragmentList.add(KeyFragment(index))
                }
            }
            mViewPagerAdapter.titleList = newTitles.map { it.name }
            mViewPagerAdapter.notifyDataSetChanged()
            Log.v("LifeCycleTest", "viewPager: ${mViewPagerAdapter.titleList}")
        })
    }

    private fun inject(){
        DaggerMainComponent.builder()
            .baseComponent((application as MyApplication).getBaseComponent())
            .mainModule(MainModule(this))
            .build()
            .inject(this)
    }

    companion object{
        @JvmStatic
        fun startThisActivity(context: Context, needCheckFingerPrint: Boolean=true){
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("need_check_finger_print", needCheckFingerPrint)
            context.startActivity(intent)
        }
    }
}
