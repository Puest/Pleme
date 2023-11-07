package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button

class sucess_f(context: Context) {
    private val sucess_f = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        sucess_f.setContentView(R.layout.sucess_f)
        sucess_f.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        sucess_f.setCanceledOnTouchOutside(true)
        sucess_f.setCancelable(true)

        val confirmButton = sucess_f.findViewById<Button>(R.id.ch_out)
        confirmButton.setOnClickListener {
            onClickListener.onClicked()
            sucess_f.dismiss()
        }

        sucess_f.show()
    }

    interface OnDialogClickListener {
        fun onClicked()
    }
}
