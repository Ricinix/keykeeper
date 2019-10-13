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
import kotlinx.android.synthetic.main.edit_dialog.*

class KeyEditDialog(context: Context, private val listener: Listener): Dialog(context) {

    private var pwdKind = MIX

    private val textWatcher = object :TextWatcher{
        override fun afterTextChanged(p0: Editable?) {
            switch_rand_pwd.isChecked = false
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_dialog)

        setWidth()
        setBtnListener()
        setCanceledOnTouchOutside(false)
    }

    private fun setWidth(){
        val mWindowManager = window?.windowManager
        val display = mWindowManager?.defaultDisplay
        val params = window?.attributes
        val size = Point()
        display?.getSize(size)
        params?.width = (size.x * 0.9).toInt()
        window?.attributes = params
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setBtnListener(){
        confirm_btn.setOnClickListener {
            Log.v("BtnTest", "confirm press")
            val nameInput = name_input.text.toString()
            val accountInput = account_input.text.toString()
            val passwordInput = if (pwdKind == MIX){
                pwd_input_mix.text.toString()
            }else {
                pwd_input_num.text.toString()
            }
            if (nameInput != "" && accountInput != "" && passwordInput != ""){
                listener.onConfirm(nameInput, accountInput, passwordInput, pwdKind)
                dismiss()
            }
            else
                Toast.makeText(context, "输入为空", Toast.LENGTH_LONG).show()
        }
        cancel_btn.setOnClickListener {
            Log.v("BtnTest", "cancel press")
            listener.onCancel()
            dismiss()
        }
        radio_group.setOnCheckedChangeListener { _, id ->
            when (id){
                R.id.radio_btn_mix_pwd -> {
                    pwdKind = MIX
                    val isFocus = pwd_input_num.isFocused
                    num_input_layout.visibility = View.GONE
                    mix_input_layout.visibility = View.VISIBLE
                    pwd_input_mix.text = pwd_input_num.text
                    if (isFocus)
                        pwd_input_mix.requestFocus()
                }

                R.id.radio_btn_num_pwd -> {
                    pwdKind = NUM
                    val isFocus = pwd_input_mix.isFocused
                    mix_input_layout.visibility = View.GONE
                    num_input_layout.visibility = View.VISIBLE
                    val matcher = Regex("\\d+").find(pwd_input_mix.text.toString())
                    pwd_input_num.text = SpannableStringBuilder(matcher?.value?:"")
                    if (isFocus)
                        pwd_input_num.requestFocus()
                }
            }
        }
        switch_rand_pwd.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck){
                Log.v("BtnTest", "switch check")
                pwd_input_num.removeTextChangedListener(textWatcher)
                pwd_input_mix.removeTextChangedListener(textWatcher)

                if (pwdKind == MIX){
                    val oldTextLength = pwd_input_mix.text.toString().length
                    val newText = Random.getRandomPlainText(oldTextLength)
                    pwd_input_mix.text = SpannableStringBuilder(newText)
                }else{
                    val oldTextLength = pwd_input_num.text.toString().length
                    val newText = Random.getRandomPlainText(oldTextLength)
                    pwd_input_num.text = SpannableStringBuilder(newText)
                }

                pwd_input_num.addTextChangedListener(textWatcher)
                pwd_input_mix.addTextChangedListener(textWatcher)
            }
            else {
                Log.v("BtnTest", "switch uncheck")
            }

        }
    }

    interface Listener{
        fun onConfirm(name:String, account:String, password:String, kind:String)
        fun onCancel()
    }

    companion object{
        const val MIX = "mix_password"
        const val NUM = "num_password"
    }

}