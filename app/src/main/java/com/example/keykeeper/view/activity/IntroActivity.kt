package com.example.keykeeper.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.widget.addTextChangedListener
import com.example.keykeeper.R
import com.jaeger.library.StatusBarUtil

class IntroActivity : AppCompatActivity() {
    private var pageStatus = PAGE_WELCOME
    private val passwordSpEditor by lazy {
        getSharedPreferences("password", Context.MODE_PRIVATE).edit()
    }
    private var corePassword: Editable? = null
    private var lockPassword: Editable? = null
    private var fingerEnable = true
    private var fingerAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_1)
        StatusBarUtil.setTransparent(this)
        findViewById<ImageButton>(R.id.next_btn_1).setOnClickListener { onNext() }
        fingerAvailable = FingerprintManagerCompat.from(this).run {
            isHardwareDetected && hasEnrolledFingerprints()
        }
        fingerEnable = fingerAvailable
            Log.d("IntroTest", "fingerAvailable: $fingerAvailable")
    }

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

    private fun onPrevious() {
        when (pageStatus) {
            PAGE_CORE_PASSWORD -> showWelcomePage()
            PAGE_LOCK_PASSWORD -> showCorePasswordPage()
        }
    }

    private fun showWelcomePage() {
        pageStatus = PAGE_WELCOME
        setContentView(R.layout.activity_intro_1)
        findViewById<ImageButton>(R.id.next_btn_1).setOnClickListener { onNext() }
    }

    private fun showCorePasswordPage() {
        pageStatus = PAGE_CORE_PASSWORD
        setContentView(R.layout.activity_intro_2)
        val nextBtn = findViewById<ImageButton>(R.id.next_btn_2)
        val inputPassword = findViewById<AppCompatEditText>(R.id.input_core_pwd)
        inputPassword.addTextChangedListener {
            if (it?.isNotEmpty() == true) {
                nextBtn.visibility = View.VISIBLE
            } else {
                nextBtn.visibility = View.GONE
            }
        }
        if (corePassword?.isNotEmpty() == true) {
            inputPassword.run {
                text = corePassword
//                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT
            }
        }
        findViewById<ImageButton>(R.id.hide_core_pwd).setOnTouchListener { v, event ->
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
        findViewById<ImageButton>(R.id.previous_btn_2).setOnClickListener {
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
            findViewById<AppCompatCheckBox>(R.id.checkbox_enable_fingerprint).apply {
                isChecked = fingerEnable
                if (!fingerAvailable) {
                    isEnabled = false
                }
            }
        val nextBtn = findViewById<AppCompatImageButton>(R.id.next_btn_3)
        val inputPassword = findViewById<AppCompatEditText>(R.id.input_lock_pwd)
        inputPassword.addTextChangedListener {
            if (it?.length?:0 >= 6) {
                nextBtn.visibility = View.VISIBLE
                if (it?.length?:0 > 6) {
                    val tempText = inputPassword.text
                    inputPassword.text = tempText?.subSequence(0, 6) as Editable
                    inputPassword.setSelection(6)
                }
            } else {
                nextBtn.visibility = View.GONE
            }
        }
        findViewById<AppCompatImageButton>(R.id.hide_lock_pwd).setOnTouchListener { v, event ->
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
            passwordSpEditor.putString("lock_pwd", lockPassword.toString())
            passwordSpEditor.putBoolean("finger_enable", fingerEnable)
            passwordSpEditor.commit()
            onNext()
        }
        findViewById<AppCompatImageButton>(R.id.previous_btn_3).setOnClickListener {
            fingerEnable = finggerCheckBox.isChecked
            lockPassword = inputPassword.text
            onPrevious()
        }
    }

    private fun showFinishPage() {
        pageStatus = PAGE_FINISH
        setContentView(R.layout.activity_intro_4)
        findViewById<AppCompatImageButton>(R.id.next_btn_4).setOnClickListener { onNext() }
    }

    override fun onBackPressed() {
        if (pageStatus == PAGE_WELCOME || pageStatus == PAGE_FINISH) {
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
