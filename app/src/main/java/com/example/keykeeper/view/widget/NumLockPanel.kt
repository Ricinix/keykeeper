package com.example.keykeeper.view.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_number_panel.view.*
import kotlinx.coroutines.*

class NumLockPanel(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attributeSet, defStyleAttr) {
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private val scope = MainScope()

    // 外部判断密码是否匹配
    var onCheckPassword: (String) -> Boolean = { false }

    // 用sb可以省内存
    private val password = StringBuilder()

    private val passwordHandle = Handler {
        when (it.what) {
            DELETE_PWD -> {
                // 非空就可以删除一位
                if (password.isNotEmpty()) {
                    password.run { deleteCharAt(lastIndex) }
                    processChange(false)
                }
            }
            APPEND_PWD -> {
                // 小于6位就加一位
                if (password.length < 6) {
                    password.append(it.obj)
                    processChange(true)
                }
                // 等于6位就判断密码是否匹配
                if (password.length == 6) {
                    onCheckNumPassword()
                }
            }
        }
        true
    }

    // 成功加载的时候执行
    override fun onFinishInflate() {
        super.onFinishInflate()
        setMyListener()
    }

    private fun setMyListener() {
        btn_number_0.setOnClickListener { pwdAppend("0") }
        btn_number_1.setOnClickListener { pwdAppend("1") }
        btn_number_2.setOnClickListener { pwdAppend("2") }
        btn_number_3.setOnClickListener { pwdAppend("3") }
        btn_number_4.setOnClickListener { pwdAppend("4") }
        btn_number_5.setOnClickListener { pwdAppend("5") }
        btn_number_6.setOnClickListener { pwdAppend("6") }
        btn_number_7.setOnClickListener { pwdAppend("7") }
        btn_number_8.setOnClickListener { pwdAppend("8") }
        btn_number_9.setOnClickListener { pwdAppend("9") }
        btn_number_back.setOnClickListener { pwdRemove() }
    }

    private fun onCheckNumPassword() {
        scope.launch {
            // 延时一小会，以免最后一个实心圆显示不出来
            delay(100)
            for (i in linearLayout_number_panel.childCount - 1 downTo 0) {
                val circleView = linearLayout_number_panel.getChildAt(i) as CircleImageView
                circleView.setStroke()
            }
            if (!onCheckPassword(password.toString())) {
                Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
            }
            password.clear()
        }
    }

    /**
     * [add] true表示添加一位，false表示删除一位
     */
    private fun processChange(add: Boolean) {
        if (add) {
            val circleView =
                linearLayout_number_panel.getChildAt(password.length - 1) as CircleImageView
            circleView.setFill()
        } else {
            val circleView = linearLayout_number_panel.getChildAt(password.length) as CircleImageView
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

    // 在生命周期的最后一步撤销scope，以免内存泄漏
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }


    companion object {
        const val TAG = "NumLockTest"
        const val APPEND_PWD = 0
        const val DELETE_PWD = 1
    }
}