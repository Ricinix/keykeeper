package com.example.keykeeper.view.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

open class BaseActivity: AppCompatActivity() {
    private var fingerCheckCode = FROM_BACK
    private lateinit var mainLayoutView : View
    private lateinit var coverLayoutView : View
    private lateinit var fingerPrint: FingerprintManagerCompat
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
    }

    protected fun setMyContentView(mainLayoutView : View, coverLayoutView: View){
        this.mainLayoutView = mainLayoutView
        this.coverLayoutView = coverLayoutView
    }

    override fun onRestart() {
        super.onRestart()
        when (fingerCheckCode){
            FROM_BACK -> checkFingerPrint()
            FROM_ACTIVITY -> fingerCheckCode = FROM_BACK
        }
    }

    private fun showNumLockPanel(){}

    open fun onFingerPrintCheckSucceed(){
        mainLayoutView.visibility = View.VISIBLE
        coverLayoutView.visibility = View.GONE
        fingerCheckCode = FROM_BACK
    }

    open fun onFingerPrintCheckFail(){
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
            onFingerPrintCheckFail()
        }
    }

    companion object {
        const val FROM_BACK = 0
        const val FROM_ACTIVITY = 1
        const val FROM_CHECK = 2


        const val CHECK_WRONG = 0
        const val CHECK_SUCCEED = 1
        const val CHECK_FAIL = 2
    }
}