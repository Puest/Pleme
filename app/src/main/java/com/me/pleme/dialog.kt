package com.me.pleme
import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.EditText

class dialog(context: Context){
    private val dialog = Dialog(context)
    private lateinit var onClickListner : OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener){
        onClickListner = listener
    }
    fun showDialog(){
        dialog.setContentView(R.layout.dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()
    }

    interface OnDialogClickListener{
        fun onCliked(name:String)
    }
}