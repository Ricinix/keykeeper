package com.example.keykeeper.view.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import com.example.keykeeper.R


class FingerPrintDialog(context: Context): Dialog(context, R.style.Dialog_FullScreen) {


    private lateinit var mListener: OnLockListener

    fun setListener(onLockListener: OnLockListener){
        mListener = onLockListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_finger_print)
        setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        checkFingerPrint()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mListener.onFail()
    }

    private fun checkFingerPrint(){
        val fingerPrint = FingerprintManagerCompat.from(context)
        val handler = Handler{
            when (it.what){
                CHECK_SUCCEED -> {
                    mListener.onSucceed()
                    dismiss()
                }
                CHECK_FAIL -> showNumLockPanel()
            }
            true
        }
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
    }

    private fun showNumLockPanel(){

    }

    interface OnLockListener{
        fun onSucceed()
        fun onFail()
    }

    companion object{
        const val CHECK_WRONG = 0
        const val CHECK_SUCCEED = 1
        const val CHECK_FAIL = 2
    }

}