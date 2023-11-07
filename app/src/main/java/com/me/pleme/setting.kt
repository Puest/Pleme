package com.me.pleme

import android.app.Dialog
import android.content.Context
import android.view.WindowManager

class setting(context: Context) {
    val setting_d = Dialog(context)
// 팝업창 실행
    fun showDialog(){
        setting_d.setContentView(R.layout.setting)
        setting_d.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT)
        setting_d.setCanceledOnTouchOutside(true)
        setting_d.setCancelable(true)
        setting_d.show()
    }
}