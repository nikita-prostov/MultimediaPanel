package com.nks.interactive.multimediapanel.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.ui.components.NotificationType
import com.nks.interactive.multimediapanel.ui.components.PopupNotification
import com.nks.interactive.multimediapanel.ui.screens.settings.SettingsScreen
import com.nks.interactive.multimediapanel.ui.theme.ClientTheme
import com.nks.interactive.multimediapanel.ui.utils.fullscreen
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.fullscreen()
        setContent {
            ClientTheme {
                val appSetting = koinInject<AppDataStorage>()
                var showSettings by remember { mutableStateOf(appSetting.ipAddress.isEmpty() || appSetting.port.isEmpty()) }
                var showNotification by remember { mutableStateOf(false) }

                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    if(showSettings){
                        SettingsScreen{showSettings = false}
                    }
                    AnimatedVisibility(
                        visible = showNotification,
                        modifier = Modifier.align(Alignment.TopCenter),
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        PopupNotification("Test","test", NotificationType.Critical)
                    }
                }
            }
        }
    }
}