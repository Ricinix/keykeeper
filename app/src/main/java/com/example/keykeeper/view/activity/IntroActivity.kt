package com.example.keykeeper.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.widget.addTextChangedListener
import com.example.keykeeper.R
import com.jaeger.library.StatusBarUtil

class IntroActivity : AppCompatActivity() {
    // 状态转换码，判断现在在哪个页面
    private var pageStatus = PAGE_WELCOME
    // 8说了，延迟初始化就完事了
    private val passwordSpEditor by lazy {
        getSharedPreferences("password", Context.MODE_PRIVATE).edit()
    }
    // 记录核心密钥及解锁数字密码，你问我为什么不用String？
    // Editable是String的上转型对象，赋值回EditText中更方便些
    private var corePassword: Editable? = null
    private var lockPassword: Editable? = null
    // 用户是否选择启用指纹解锁
    private var fingerEnable = true
    // 硬件是否支持、是否录入指纹。若此处为false，则enable一定也是false
    private var fingerAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_1)
        // 设置状态栏沉浸
        StatusBarUtil.setTransparent(this)
        findViewById<AppCompatImageButton>(R.id.btn_intro_next_1).setOnClickListener { onNext() }

        fingerAvailable = FingerprintManagerCompat.from(this).run {
            isHardwareDetected && hasEnrolledFingerprints()
        }
        fingerEnable = fingerAvailable
        Log.d("IntroTest", "fingerAvailable: $fingerAvailable")
    }

    // 下一页
    private fun onNext() {
        Log.d("IntroTest", "pageStatus: $pageStatus")
        when (pageStatus) {
            PAGE_WELCOME -> showCorePasswordPage()
            PAGE_CORE_PASSWORD -> showLockPasswordPage()
            PAGE_LOCK_PASSWORD -> showFinishPage()
            PAGE_FINISH -> {
                MainActivity.startThisActivity(this, false)
                finish()
            }
        }
        StatusBarUtil.setTransparent(this)
    }

    // 上一页，但是welcome页面和finish页面不能返回上一页
    private fun onPrevious() {
        when (pageStatus) {
            PAGE_CORE_PASSWORD -> showWelcomePage()
            PAGE_LOCK_PASSWORD -> showCorePasswordPage()
            PAGE_FINISH -> {
                MainActivity.startThisActivity(this, false)
                finish()
            }
        }
    }

    private fun showWelcomePage() {
        pageStatus = PAGE_WELCOME
        setContentView(R.layout.activity_intro_1)
        findViewById<AppCompatImageButton>(R.id.btn_intro_next_1).setOnClickListener { onNext() }
    }

    private fun showCorePasswordPage() {
        pageStatus = PAGE_CORE_PASSWORD
        setContentView(R.layout.activity_intro_2)
        val nextBtn = findViewById<AppCompatImageButton>(R.id.btn_intro_next_2)
        val inputPassword = findViewById<AppCompatEditText>(R.id.input_intro_core_pwd)
        // 如果不是第一次来此页，则填上以前填过的核心密钥
        if (corePassword?.isNotEmpty() == true) {
            inputPassword.text = corePassword
        }
        // 只有填入了核心密钥才显示下一页按钮
        inputPassword.addTextChangedListener {
            if (it?.isNotEmpty() == true) {
                nextBtn.visibility = View.VISIBLE
            } else {
                nextBtn.visibility = View.GONE
            }
        }
        // 按住查看密码。最后返回false以防止事件在此被消费，从而导致selector不起作用
        findViewById<AppCompatImageButton>(R.id.btn_intro_hide_core_pwd).setOnTouchListener { v, event ->
            Log.d("IntroTest", "type: ${inputPassword.inputType}");
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    inputPassword.inputType = InputType.TYPE_CLASS_TEXT
                }
                MotionEvent.ACTION_UP -> {
                    inputPassword.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT
                    v.performClick()
                }
            }
            false
        }
        findViewById<AppCompatImageButton>(R.id.btn_intro_previous_2).setOnClickListener {
            corePassword = inputPassword.text
            onPrevious()
        }
        nextBtn.setOnClickListener {
            corePassword = inputPassword.text
            passwordSpEditor.run {
                putString("core_pwd", corePassword.toString())
                commit()
            }
            onNext()
        }
    }

    private fun showLockPasswordPage() {
        pageStatus = PAGE_LOCK_PASSWORD
        setContentView(R.layout.activity_intro_3)
        val finggerCheckBox =
            findViewById<AppCompatCheckBox>(R.id.checkbox_intro_enable_fingerprint).apply {
                isChecked = fingerEnable
                // 如果不能使用则禁用此checkbox
                if (!fingerAvailable) {
                    isEnabled = false
                }
            }
        val nextBtn = findViewById<AppCompatImageButton>(R.id.btn_intro_next_3)
        val inputPassword = findViewById<AppCompatEditText>(R.id.input_intro_lock_pwd)
        inputPassword.addTextChangedListener {
            // 保持密码长度为6位
            if (it?.length ?: 0 >= 6) {
                nextBtn.visibility = View.VISIBLE
                if (it?.length ?: 0 > 6) {
                    val tempText = inputPassword.text
                    inputPassword.text = tempText?.subSequence(0, 6) as Editable
                    inputPassword.setSelection(6)
                }
            } else {
                nextBtn.visibility = View.GONE
            }
        }
        // 依旧是按下看密码
        findViewById<AppCompatImageButton>(R.id.btn_intro_hide_lock_pwd).setOnTouchListener { v, event ->
            Log.d("IntroTest", "type: ${inputPassword.inputType}");
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    inputPassword.inputType = InputType.TYPE_CLASS_NUMBER
                }
                MotionEvent.ACTION_UP -> {
                    inputPassword.inputType =
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD + InputType.TYPE_CLASS_NUMBER
                    v.performClick()
                }
            }
            false
        }
        nextBtn.setOnClickListener {
            fingerEnable = finggerCheckBox.isChecked
            lockPassword = inputPassword.text
            // 切换下一页时则提交存储，防止最后一起存储导致卡顿
            passwordSpEditor.putString("lock_pwd", lockPassword.toString())
            passwordSpEditor.putBoolean("finger_enable", fingerEnable)
            passwordSpEditor.commit()
            onNext()
        }
        findViewById<AppCompatImageButton>(R.id.btn_intro_previous_3).setOnClickListener {
            fingerEnable = finggerCheckBox.isChecked
            lockPassword = inputPassword.text
            onPrevious()
        }
    }

    private fun showFinishPage() {
        pageStatus = PAGE_FINISH
        setContentView(R.layout.activity_intro_4)
        findViewById<AppCompatImageButton>(R.id.btn_intro_next_4).setOnClickListener { onNext() }
    }

    override fun onBackPressed() {
        // 第一页直接退出
        if (pageStatus == PAGE_WELCOME) {
            super.onBackPressed()
        } else {
            onPrevious()
        }
    }

    companion object {
        fun startThisActivity(context: Context) {
            val intent = Intent(context, IntroActivity::class.java)
            context.startActivity(intent)
        }

        const val PAGE_WELCOME = 0
        const val PAGE_CORE_PASSWORD = 1
        const val PAGE_LOCK_PASSWORD = 2
        const val PAGE_FINISH = 3
    }
}
