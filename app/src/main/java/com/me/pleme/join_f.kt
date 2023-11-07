package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.view.WindowManager
// 회원가입 실패 시 뜨는 팝업창
class join_f(context: Context) {
    private  val join_f = Dialog(context)
    private lateinit var onClickListener: OnClickListener

    fun setOnClickListener(listener: OnClickListener){
        onClickListener = listener

    }
    fun showDialog(){
        join_f.setContentView(R.layout.join_f)
        join_f.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        join_f.setCanceledOnTouchOutside(true)
        join_f.setCancelable(true)
        join_f.show()
    }
    interface OnDialogClickListener{
        fun OnClicked(name:String)
    }
}