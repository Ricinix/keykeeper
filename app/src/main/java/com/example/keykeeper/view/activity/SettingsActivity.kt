package com.example.keykeeper.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.keykeeper.R
import com.example.keykeeper.view.adapter.GeneralSettingRecyclerAdapter
import com.example.keykeeper.view.fragment.DetailSettingFragment
import com.example.keykeeper.view.fragment.GeneralSettingFragment
import com.example.keykeeper.view.fragment.TitleSettingFragment
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : BaseActivity() {

    private val generalSettingFragment = GeneralSettingFragment()
    private val detailSettingFragment = DetailSettingFragment()
    private val titleSettingFragment = TitleSettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.settings_activity)
        setMyContentView(main_layout2, cover_layout2, number_panel_layout2)
        setSupportActionBar(toolbar)
        changeToGeneralSetting()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setObserver()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else{
            if (isTaskRoot)
                MainActivity.startThisActivity(this)
            else
                finish()
        }
    }

    private fun setObserver(){
        generalSettingFragment.fragChangeId.observe(this, Observer { id->
            when (id){
                GeneralSettingRecyclerAdapter.TITLE_SETTING->{
                    changeToTitleSetting()
                }
                GeneralSettingRecyclerAdapter.DETAIL_SETTING->{
                    changeToDetailSetting()
                }
            }
        })
    }

    private fun changeToTitleSetting(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, titleSettingFragment, "title")
            .addToBackStack("general")
            .commit()
    }

    private fun changeToDetailSetting(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, detailSettingFragment, "detail")
            .addToBackStack("general")
            .commit()
    }

    private fun changeToGeneralSetting(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, generalSettingFragment, "general")
            .commit()
    }

    companion object{
        private const val FROM_OTHER_ACTIVITY = 1
        @JvmStatic
        fun startThisActivity(context: Context){
            val intent = Intent(context, SettingsActivity::class.java)
            intent.putExtra("intent", FROM_OTHER_ACTIVITY)
            context.startActivity(intent)
        }
    }
}