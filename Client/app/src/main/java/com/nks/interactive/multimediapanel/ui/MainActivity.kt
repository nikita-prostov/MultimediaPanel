package com.nks.interactive.multimediapanel.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nks.interactive.multimediapanel.ui.theme.ClientTheme
import com.nks.interactive.multimediapanel.ui.utils.fullscreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.fullscreen()
        setContent {
            ClientTheme {

            }
        }
    }
}