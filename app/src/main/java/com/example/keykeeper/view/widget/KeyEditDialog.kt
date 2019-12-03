package com.example.keykeeper.view.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.keykeeper.R
import com.example.keykeeper.domain.Random
import kotlinx.android.synthetic.main.dialog_key.*

class KeyEditDialog(context: Context, private val listener: Listener) : Dialog(context) {

    // 密码的类别（混合或者是纯数字）
    private var pwdKind = MIX

    private val textWatcher = object : TextWatcher {
        // 若手动修改了，那么就不算是随机密码
        // 在这里取消掉选中，方便用户再点一次
        override fun afterTextChanged(p0: Editable?) {
            switch_dialog_key_rand_pwd.isChecked = false
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_key)

        setWidth()
        setAnim()
        setBtnListener()
        setCanceledOnTouchOutside(false)
    }

    /**
     * 设置进场退场动画
     */
    private fun setAnim() {
        window?.setWindowAnimations(R.style.KeyDialogAnimation)
    }

    /**
     * 设置宽度为0.9倍
     */
    private fun setWidth() {
        val mWindowManager = window?.windowManager
        val display = mWindowManager?.defaultDisplay
        // 获取属性集
        val params = window?.attributes
        val size = Point()
        // 获取size
        display?.getSize(size)
        params?.width = (size.x * 0.9).toInt()
        window?.attributes = params
        // 设置背景透明，不然圆角效果不好
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setBtnListener() {
        btn_dialog_key_confirm.setOnClickListener {
            Log.v("BtnTest", "confirm press")
            val nameInput = input_dialog_key_name.text.toString()
            val accountInput = input_dialog_key_account.text.toString()
            // 通过密码类型来从不同的view中获取密码
            val passwordInput = if (pwdKind == MIX) {
                input_dialog_key_mix_pwd.text.toString()
            } else {
                input_dialog_key_num_pwd.text.toString()
            }
            // 只有全部都填好了才算设置成功
            if (nameInput != "" && accountInput != "" && passwordInput != "") {
                listener.onConfirm(nameInput, accountInput, passwordInput, pwdKind)
                dismiss()
            } else {
                Toast.makeText(context, "输入为空", Toast.LENGTH_LONG).show()
            }
        }
        // 取消按钮
        btn_dialog_key_cancel.setOnClickListener {
            Log.v("BtnTest", "cancel press")
            listener.onCancel()
            dismiss()
        }
        radio_group_dialog_key.setOnCheckedChangeListener { _, id ->
            when (id) {
                // 选择了混合密码
                R.id.radio_btn_dialog_key_mix_pwd -> {
                    pwdKind = MIX
                    val isFocus = input_dialog_key_num_pwd.isFocused
                    layout_dialog_key_num_input.visibility = View.GONE
                    layout_dialog_key_mix_input.visibility = View.VISIBLE
                    input_dialog_key_mix_pwd.text = input_dialog_key_num_pwd.text
                    if (isFocus)
                        input_dialog_key_mix_pwd.run {
                            requestFocus()
                            setSelection(text?.length?:0)
                        }
                }

                // 选择了数字密码
                R.id.radio_btn_dialog_key_num_pwd -> {
                    pwdKind = NUM
                    val isFocus = input_dialog_key_mix_pwd.isFocused
                    input_dialog_key_mix_pwd.visibility = View.GONE
                    input_dialog_key_num_pwd.visibility = View.VISIBLE
                    val matcher = Regex("\\d+").find(input_dialog_key_mix_pwd.text.toString())
                    input_dialog_key_num_pwd.text = SpannableStringBuilder(matcher?.value ?: "")
                    if (isFocus)
                        input_dialog_key_num_pwd.run {
                            requestFocus()
                            setSelection(text?.length?:0)
                        }
                }
            }
        }
        // 随机密码按钮
        switch_dialog_key_rand_pwd.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                Log.v("BtnTest", "switch check")
                // 要先移除监听，不然一选中，然后触发更改，就会选中又没了
                input_dialog_key_num_pwd.removeTextChangedListener(textWatcher)
                input_dialog_key_mix_pwd.removeTextChangedListener(textWatcher)

                if (pwdKind == MIX) {
                    val oldTextLength = input_dialog_key_mix_pwd.text.toString().length
                    val newText = Random.getRandomPlainText(oldTextLength)
                    input_dialog_key_mix_pwd.text = SpannableStringBuilder(newText)
                } else {
                    val oldTextLength = input_dialog_key_num_pwd.text.toString().length
                    val newText = Random.getRandomNumberText(oldTextLength)
                    input_dialog_key_num_pwd.text = SpannableStringBuilder(newText)
                }

                input_dialog_key_num_pwd.addTextChangedListener(textWatcher)
                input_dialog_key_mix_pwd.addTextChangedListener(textWatcher)
            } else {
                Log.v("BtnTest", "switch uncheck")
            }

        }
    }

    /**
     * 通过外部来设置相关控件的数据显示，一般是用于修改key的时候
     */
    fun setMessage(name: String, account: String, password: String, kind: String) {
        input_dialog_key_name.text = SpannableStringBuilder(name)
        input_dialog_key_account.text = SpannableStringBuilder(account)
        if (kind == MIX) {
            radio_btn_dialog_key_mix_pwd.isChecked = true
            input_dialog_key_mix_pwd.text = SpannableStringBuilder(password)
        } else {
            radio_btn_dialog_key_num_pwd.isChecked = true
            input_dialog_key_num_pwd.text = SpannableStringBuilder(password)
        }
    }

    interface Listener {
        fun onConfirm(name: String, account: String, password: String, kind: String)
        fun onCancel()
    }

    companion object {
        const val MIX = "mix_password"
        const val NUM = "num_password"
    }

}