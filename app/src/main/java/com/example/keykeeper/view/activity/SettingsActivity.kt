package com.example.keykeeper.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.keykeeper.R
import com.example.keykeeper.view.adapter.GeneralSettingRecyclerAdapter
import com.example.keykeeper.view.fragment.DetailSettingFragment
import com.example.keykeeper.view.fragment.GeneralSettingFragment
import com.example.keykeeper.view.fragment.TitleSettingFragment
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    private val generalSettingFragment = GeneralSettingFragment()
    private val detailSettingFragment = DetailSettingFragment()
    private val titleSettingFragment = TitleSettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_settings)
        setMyContentView(layout_setting, layout_cover_2, layout_number_panel_2)
        setSupportActionBar(toolbar_setting)
        changeToGeneralSetting()
        // 设置左上角的三杠键
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setObserver()
    }

    override fun onBackPressed() {
        // 若还有fragment在堆栈中，就弹出该fragment，否则返回MainActivity
        if (isAtCheck()) {
            Toast.makeText(this, "请验证身份", Toast.LENGTH_SHORT).show()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (isTaskRoot)
                MainActivity.startThisActivity(this)
            else
                finish()
        }
    }

    private fun setObserver() {
        // 从一般设置中选择进入哪个设置页面
        generalSettingFragment.fragChangeId.observe(this, Observer { id ->
            when (id) {
                GeneralSettingRecyclerAdapter.TITLE_SETTING -> {
                    changeToTitleSetting()
                }
                GeneralSettingRecyclerAdapter.DETAIL_SETTING -> {
                    changeToDetailSetting()
                }
            }
        })
    }

    private fun changeToTitleSetting() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_setting, titleSettingFragment, "title")
            .addToBackStack("general")
            .commit()
    }

    private fun changeToDetailSetting() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_setting, detailSettingFragment, "detail")
            .addToBackStack("general")
            .commit()
    }

    private fun changeToGeneralSetting() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_setting, generalSettingFragment, "general")
            .commit()
    }

    companion object {
        @JvmStatic
        fun startThisActivity(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }
}