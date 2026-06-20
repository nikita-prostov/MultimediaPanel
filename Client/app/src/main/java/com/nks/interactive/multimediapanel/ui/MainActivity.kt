package com.nks.interactive.multimediapanel.ui

import android.os.Bundle
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nks.interactive.multimediapanel.api.notification.NotificationSseClient
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.models.notification.NotificationDto
import com.nks.interactive.multimediapanel.models.notification.NotificationType
import com.nks.interactive.multimediapanel.ui.components.PopupNotification
import com.nks.interactive.multimediapanel.ui.screens.main.MainScreen
import com.nks.interactive.multimediapanel.ui.screens.settings.SettingsScreen
import com.nks.interactive.multimediapanel.ui.theme.ClientTheme
import com.nks.interactive.multimediapanel.ui.utils.fullscreen
import com.nks.interactive.multimediapanel.viewModel.MainActivityVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.fullscreen()
        setContent {
            ClientTheme {
                val vm = koinViewModel<MainActivityVM>()

                LaunchedEffect(Unit) { vm.connect() }
                DisposableEffect(Unit) {
                    onDispose { vm.disconnect() }
                }

                val showSettings by vm.showSettings
                var notification by vm.notification

                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    if(showSettings) SettingsScreen{vm.showSettings.value = false}
                    else MainScreen(modifier = Modifier.align(Alignment.Center))

                    AnimatedVisibility(
                        visible = notification != null,
                        modifier = Modifier.align(Alignment.TopCenter),
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    ) {
                        PopupNotification(
                            title = notification?.title ?: "",
                            subTitle = notification?.subTitle ?: "",
                            type = notification?.type ?: NotificationType.None)
                    }
                }
            }
        }
    }
}