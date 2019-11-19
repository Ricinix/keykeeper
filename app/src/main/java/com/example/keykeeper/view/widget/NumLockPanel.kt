package com.example.keykeeper.view.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_panel_layout.view.*
import kotlinx.coroutines.*

class NumLockPanel(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attributeSet, defStyleAttr) {
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private lateinit var scope: CoroutineScope

    var onCheckPassword: (String) -> Boolean = { false }

    private val password = StringBuilder()

    private val passwordHandle = Handler {
        when (it.what) {
            DELETE_PWD -> {
                if (password.isNotEmpty()) {
                    password.run { deleteCharAt(lastIndex) }
                    processChange(false)
                }
            }
            APPEND_PWD -> {
                if (password.length < 6) {
                    password.append(it.obj)
                    processChange(true)
                }
                if (password.length == 6) {
                    onCheckNumPassword()
                }
            }
        }
        true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setMyListener()
    }

    private fun setMyListener() {
        number_btn_0.setOnClickListener { pwdAppend("0") }
        number_btn_1.setOnClickListener { pwdAppend("1") }
        number_btn_2.setOnClickListener { pwdAppend("2") }
        number_btn_3.setOnClickListener { pwdAppend("3") }
        number_btn_4.setOnClickListener { pwdAppend("4") }
        number_btn_5.setOnClickListener { pwdAppend("5") }
        number_btn_6.setOnClickListener { pwdAppend("6") }
        number_btn_7.setOnClickListener { pwdAppend("7") }
        number_btn_8.setOnClickListener { pwdAppend("8") }
        number_btn_9.setOnClickListener { pwdAppend("9") }
        number_btn_back.setOnClickListener { pwdRemove() }
    }

    private fun onCheckNumPassword(){
        scope.launch {
            delay(100)
            for (i in linearLayout.childCount - 1 downTo 0) {
                val circleView = linearLayout.getChildAt(i) as CircleImageView
                circleView.setStroke()
            }
            if (!onCheckPassword(password.toString())) {
                Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
            }
            password.clear()
        }
    }

    private fun processChange(add: Boolean) {
        if (add) {
            val circleView =
                linearLayout.getChildAt(password.length - 1) as CircleImageView
            circleView.setFill()
        } else {
            val circleView = linearLayout.getChildAt(password.length) as CircleImageView
            circleView.setStroke()
        }
    }

    private fun pwdAppend(pwd: String) {
        val msg = passwordHandle.obtainMessage(APPEND_PWD)
        msg.obj = pwd
        msg.sendToTarget()
    }

    private fun pwdRemove() {
        val msg = passwordHandle.obtainMessage(DELETE_PWD)
        msg.sendToTarget()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scope = MainScope()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }


    companion object {
        const val APPEND_PWD = 0
        const val DELETE_PWD = 1
    }
}