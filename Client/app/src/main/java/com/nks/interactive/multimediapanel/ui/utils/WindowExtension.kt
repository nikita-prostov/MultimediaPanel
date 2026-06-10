package com.nks.interactive.multimediapanel.ui.utils

import android.view.View
import android.view.Window
import android.view.WindowManager

@Suppress("DEPRECATION")
fun Window.fullscreen(): Unit {
    this.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
    this.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    this.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
}