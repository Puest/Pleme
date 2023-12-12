package com.me.pleme
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class dialog3(context: Context){
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private lateinit var onSendClickListener: OnSendClickListener
    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener

    }
    fun setOnSendClickListener(listener: OnSendClickListener) {
        onSendClickListener = listener
    }

    fun showDialog(){
        dialog.setContentView(R.layout.dialog3)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val sendButton = dialog.findViewById<Button>(R.id.no_ye)
        sendButton.setOnClickListener {
            onSendClickListener.onSendClicked()
        }

        val cancelButton = dialog.findViewById<TextView>(R.id.cancle)
        cancelButton.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

    interface OnDialogClickListener{
        fun onCanceled()
    }
    interface OnSendClickListener {
        fun onSendClicked()
    }

}