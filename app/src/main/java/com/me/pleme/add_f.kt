package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class add_f(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onConfirmClickListener: OnConfirmClickListener

    fun setOnConfirmClickListener(listener: OnConfirmClickListener) {
        onConfirmClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.add_f)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val confirmButton = dialog.findViewById<Button>(R.id.ch)
        confirmButton.setOnClickListener {
            onConfirmClickListener.onConfirmClicked()
        }

        val cancelButton = dialog.findViewById<TextView>(R.id.can)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    interface OnConfirmClickListener {
        fun onConfirmClicked()
    }
}