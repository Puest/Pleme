package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import com.me.pleme.dialog.*
// 회원가입 성공 시
class sucess_1(context: Context) {
    private val sucess1 = Dialog(context)
    private lateinit var onClickListener : OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener){
        onClickListener = listener
    }

    fun showDialog(){
        sucess1.setContentView(R.layout.sucess_1)
        sucess1.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        sucess1.setCanceledOnTouchOutside(true)
        sucess1.setCancelable(true)

        val confirmButton = sucess1.findViewById<Button>(R.id.back_btn)
        confirmButton.setOnClickListener {
            onClickListener.onClicked()
            sucess1.dismiss()
        }

        sucess1.show()
    }

    interface OnDialogClickListener{
        fun onClicked()
    }
}