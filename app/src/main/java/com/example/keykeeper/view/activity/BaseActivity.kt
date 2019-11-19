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
import kotlinx.android.synthetic.main.finger_print_layout.*

open class BaseActivity: AppCompatActivity() {
    private var fingerCheckCode = FROM_BACK
    private lateinit var mainLayoutView : View
    private lateinit var coverLayoutView : View
    private lateinit var numberPanelView : View
    private lateinit var fingerPrint: FingerprintManagerCompat
    private var password = ""
    private val handler = Handler{
        when (it.what){
            CHECK_SUCCEED -> onFingerPrintCheckSucceed()
            CHECK_FAIL -> onFingerPrintCheckFail()
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        fingerPrint = FingerprintManagerCompat.from(this)
        val pref = getSharedPreferences("password", Context.MODE_PRIVATE)
        password = pref.getString("lock_pwd", "123456")!!
    }

    protected fun setMyContentView(mainLayoutView : View, coverLayoutView: View, numberPanelView: View){
        this.mainLayoutView = mainLayoutView
        this.coverLayoutView = coverLayoutView
        this.numberPanelView = numberPanelView
        (this.numberPanelView as NumLockPanel).onCheckPassword = {
            val isSucceed = password == it
            if (isSucceed){
                onNumCheckSucceed()
            }
            isSucceed
        }
        finger_print_number_tips.setOnClickListener {
            showNumLockPanel()
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
        when (fingerCheckCode){
            FROM_BACK -> checkFingerPrint()
            FROM_ACTIVITY -> fingerCheckCode = FROM_BACK
        }
    }

    private fun showNumLockPanel(){
        coverLayoutView.visibility = View.GONE
        numberPanelView.visibility = View.VISIBLE
    }

    private fun onNumCheckSucceed(){
        mainLayoutView.visibility = View.VISIBLE
        numberPanelView.visibility = View.GONE
        fingerCheckCode = FROM_BACK
    }

    open fun onFingerPrintCheckSucceed(){
        mainLayoutView.visibility = View.VISIBLE
        coverLayoutView.visibility = View.GONE
        fingerCheckCode = FROM_BACK
    }

    open fun onFingerPrintCheckFail(){
        Toast.makeText(this, "错误次数已达上限", Toast.LENGTH_SHORT).show()
        showNumLockPanel()
    }

    private fun onFingerPrintCheckNotAvailable(){
        Toast.makeText(this, "该设备不支持指纹解锁", Toast.LENGTH_SHORT).show()
        showNumLockPanel()
    }

    protected fun setCheckCodeToActivity(){
        fingerCheckCode = FROM_ACTIVITY
    }

    protected fun checkFingerPrint(){
        Log.v("FingerPrintTest", "onCheck")
        fingerCheckCode = FROM_CHECK
        mainLayoutView.visibility = View.GONE
        coverLayoutView.visibility = View.VISIBLE

        if (fingerPrint.isHardwareDetected and fingerPrint.hasEnrolledFingerprints()){
            fingerPrint.authenticate(
                null,
                0,
                CancellationSignal(),
                object : FingerprintManagerCompat.AuthenticationCallback(){
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
        }
        else{
            onFingerPrintCheckNotAvailable()
        }
    }

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