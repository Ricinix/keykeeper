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
    lateinit var mainViewModel: MainViewModel

    private val mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 这么蛋疼的惭怍只是为了解决启动app时的白屏问题
        // 因为此Activity是launch Activity，启动时设置背景颜色为主题色，刚好能和解锁界面的主题色背景无缝衔接
        // 这里是设置回为白色背景
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)
        setMyContentView(layout_main, layout_cover, layout_number_panel)
        if (isNeedToIntro()) {
            IntroActivity.startThisActivity(this)
            finish()
        }
        // 若是从intro中过来，则不需要验证指纹
        if (intent.getBooleanExtra("need_check_finger_print", true)) {
            checkFingerPrint()
        }
        setSupportActionBar(tool_bar_main)
        inject()

        setViewPagerListener()
        setBtnListener()
        setObserver()
    }

    // 每次Activity从不可见变为可见时都从数据库中拿一次数据
    override fun onStart() {
        super.onStart()
        mainViewModel.getTitle()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_setting -> {
                setCheckCodeToActivity()
                SettingsActivity.startThisActivity(this)
            }
        }
        return true
    }

    private fun setBtnListener() {
        fab_main.setOnClickListener {
            // 获取现在所处于哪个Fragment
            val itemFrag =
                mViewPagerAdapter.instantiateItem(view_pager_main, view_pager_main.currentItem) as KeyFragment
            itemFrag.addNewKey()
            Log.v("BtnTest", itemFrag.title)
        }
    }

    private fun setViewPagerListener() {
        view_pager_main.adapter = mViewPagerAdapter
        layout_tab_main.setupWithViewPager(view_pager_main)
    }

    private fun setObserver() {
        mainViewModel.tabTitle.observe(this, Observer { newTitles ->
            val minLastIndex = min(mViewPagerAdapter.fragmentList.lastIndex, newTitles.lastIndex)
            // 移除掉多余的Fragment
            if (minLastIndex < mViewPagerAdapter.fragmentList.lastIndex) {
                for (index in minLastIndex + 1..mViewPagerAdapter.fragmentList.lastIndex) {
                    mViewPagerAdapter.fragmentList.removeAt(index)
                }
            }
            // 添加多的Fragment
            else if (minLastIndex < newTitles.lastIndex) {
                for (index in minLastIndex + 1..newTitles.lastIndex) {
                    mViewPagerAdapter.fragmentList.add(KeyFragment(index))
                }
            }
            mViewPagerAdapter.titleList = newTitles.map { it.name }
            mViewPagerAdapter.notifyDataSetChanged()
        })
    }

    private fun inject() {
        DaggerMainComponent.builder()
            .baseComponent((application as MyApplication).getBaseComponent())
            .mainModule(MainModule(this))
            .build()
            .inject(this)
    }

    companion object {
        @JvmStatic
        fun startThisActivity(context: Context, needCheckFingerPrint: Boolean = true) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("need_check_finger_print", needCheckFingerPrint)
            context.startActivity(intent)
        }
    }
}
