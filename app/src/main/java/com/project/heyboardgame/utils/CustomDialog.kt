package com.project.heyboardgame.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.project.heyboardgame.R

class CustomDialog(context: Context, private val onConfirmClick: () -> Unit) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)

        val confirmButton = findViewById<Button>(R.id.confirmBtn)

        confirmButton.setOnClickListener {
            onConfirmClick.invoke()
            dismiss()
        }

        setCanceledOnTouchOutside(false)
    }
}