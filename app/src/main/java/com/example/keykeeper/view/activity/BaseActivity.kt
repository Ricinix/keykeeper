package com.example.keykeeper.view.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.example.keykeeper.view.widget.NumLockPanel
import kotlinx.android.synthetic.main.layout_finger_print.*

open class BaseActivity : AppCompatActivity() {
    // 状态码，判断当前是否是从后台切回前台
    private var fingerCheckCode = FROM_BACK
    // 主布局
    private lateinit var mainLayoutView: View
    // 指纹遮盖布局
    private lateinit var coverLayoutView: View
    // 数字遮盖布局
    private lateinit var numberPanelView: View
    // 指纹
    private lateinit var fingerPrint: FingerprintManagerCompat
    // 供MainActivity调用，来判断当前是否有密码，若无密码则是第一次启动
    private var password = ""

    private var fingerEnable = true
    // 处理指纹解锁的handler
    private val handler = Handler {
        when (it.what) {
            CHECK_SUCCEED -> onFingerPrintCheckSucceed()
            CHECK_FAIL -> onFingerPrintCheckFail()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置后台任务列表不可见
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        fingerPrint = FingerprintManagerCompat.from(this)
        // 从SharedPreferences获取解锁密码以及指纹是否启用
        val pref = getSharedPreferences("password", Context.MODE_PRIVATE)
        password = pref.getString("lock_pwd", "")!!
        fingerEnable = pref.getBoolean("finger_enable", true)
    }

    /**
     * 在子类Activity中的setContentView后调用
     *
     * [mainLayoutView] : 显示的主布局
     *
     * [coverLayoutView] : 指指纹布局
     *
     * [numberPanelView] : 指数字解锁界面
     */
    protected fun setMyContentView(
        mainLayoutView: View,
        coverLayoutView: View,
        numberPanelView: View
    ) {
        this.mainLayoutView = mainLayoutView
        this.coverLayoutView = coverLayoutView
        this.numberPanelView = numberPanelView
        // 向下转型成NumLockPanel（自定义的view），并传入解锁成功的回调
        (this.numberPanelView as NumLockPanel).onCheckPassword = {
            // 判断密码是否一致
            val isSucceed = password == it
            if (isSucceed) {
                onNumCheckSucceed()
            }
            isSucceed
        }
        title_finger_print_number.setOnClickListener {
            showNumLockPanel()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
        when (fingerCheckCode) {
            FROM_BACK -> checkFingerPrint()
            FROM_ACTIVITY -> fingerCheckCode = FROM_BACK
        }
    }

    // 隐藏指纹布局，展示数字解锁布局
    private fun showNumLockPanel() {
        coverLayoutView.visibility = View.GONE
        numberPanelView.visibility = View.VISIBLE
    }

    // 隐藏数字解锁布局，展示主布局
    private fun onNumCheckSucceed() {
        mainLayoutView.visibility = View.VISIBLE
        numberPanelView.visibility = View.GONE
        fingerCheckCode = FROM_BACK
        onCheckSucceed()
    }

    // 隐藏指纹布局，展示主布局
    private fun onFingerPrintCheckSucceed() {
        mainLayoutView.visibility = View.VISIBLE
        coverLayoutView.visibility = View.GONE
        fingerCheckCode = FROM_BACK
        onCheckSucceed()
    }

    open fun onCheckSucceed() {}
    // 指纹解锁已达上限次数
    open fun onFingerPrintCheckFail() {
        Toast.makeText(this, "错误次数已达上限", Toast.LENGTH_SHORT).show()
        showNumLockPanel()
    }

    // 子类中要开启其他Activity时，设置此方法，可以防止返回该Activity时会出现指纹解锁
    protected fun setCheckCodeToActivity() {
        fingerCheckCode = FROM_ACTIVITY
    }

    // 指纹解锁，暴露给子类，因为子类可能要在onCreate中调用
    protected fun checkFingerPrint() {
        Log.v("FingerPrintTest", "onCheck")
        fingerCheckCode = FROM_CHECK
        mainLayoutView.visibility = View.GONE
        coverLayoutView.visibility = View.VISIBLE

        if (fingerEnable) {
            // 多说一句，在这里传入的handler并没有被在内部调用，内部代码只是通过这个handler获取到looper
            // 其他参数尚未配置，以后有需要再加
            fingerPrint.authenticate(
                null,
                0,
                CancellationSignal(),
                object : FingerprintManagerCompat.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        handler.obtainMessage(CHECK_SUCCEED).sendToTarget()
                    }

                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errMsgId, errString)
                        handler.obtainMessage(CHECK_WRONG).sendToTarget()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        handler.obtainMessage(CHECK_FAIL).sendToTarget()
                    }
                },
                handler
            )
        } else {
            showNumLockPanel()
        }
    }

    protected fun isAtCheck() : Boolean = fingerCheckCode == FROM_CHECK

    protected fun isNeedToIntro() : Boolean = password.isEmpty()

    companion object {
        const val TAG = "NumberLockTest"
        const val FROM_BACK = 0
        const val FROM_ACTIVITY = 1
        const val FROM_CHECK = 2

        const val CHECK_WRONG = 0
        const val CHECK_SUCCEED = 1
        const val CHECK_FAIL = 2
    }
}