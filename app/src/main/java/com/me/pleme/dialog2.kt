package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class dialog2(context: Context) {
    private val mContext: Context = context
    private lateinit var dialog: Dialog
    private lateinit var onClickListener: OnDialogClickListener

    fun showDialog(): Dialog {
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog2)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val confirmButton = dialog.findViewById<Button>(R.id.po_yes)

        // 버튼을 클릭했을 때 실행할 코드
        confirmButton.setOnClickListener {
            dialog.dismiss()// 다이얼로그를 닫는다

        }

        dialog.show()
        return dialog
    }

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    interface OnDialogClickListener {
        fun onClicked()
    }
}