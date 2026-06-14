package com.nks.interactive.multimediapanel.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.ui.MainActivity
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(onSaved: (() -> Unit)? = null) {
    val appDataStorage = koinInject<AppDataStorage>()

    var inputIp by remember { mutableStateOf(appDataStorage.ipAddress) }
    var inputPort by remember { mutableStateOf(appDataStorage.port) }
    var selectedWallpaper by remember { mutableIntStateOf(appDataStorage.wallpaperId) }
    val hasErrors = inputIp.isEmpty() || !isValidIP(inputIp) || inputPort.isEmpty() || !isValidPort(inputPort)

    val wallpapers = listOf(
        R.drawable.wallpaper1,
        R.drawable.wallpaper2,
        R.drawable.wallpaper3,
        R.drawable.wallpaper4
    )

    Column(Modifier.fillMaxSize()) {
        Toolbar("Настройки")
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(8.dp)) {
            // IP
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputIp,
                maxLines = 1,
                isError = inputIp.isNotEmpty() && !isValidIP(inputIp),
                onValueChange = { inputIp = it },
                label = { Text("Введите ip адрес") },
                placeholder = { Text("192.168.1.100") },
            )
            Spacer(Modifier.height(16.dp))
            // Порт
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputPort,
                maxLines = 1,
                isError = inputPort.isNotEmpty() && !isValidPort(inputPort),
                onValueChange = { inputPort = it },
                label = { Text("Введите порт") },
                placeholder = { Text("5110") }
            )
            Spacer(Modifier.height(24.dp))
            // Обои
            Text(
                "Обои",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            // Сетка с превью обоев
            var rowIndex = 0
            while (rowIndex < wallpapers.size) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..1) {
                        val index = rowIndex + col
                        if (index < wallpapers.size) {
                            val wallpaper = wallpapers[index]
                            OutlinedCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(120.dp),
                                onClick = { selectedWallpaper = wallpaper },
                                border = if (selectedWallpaper == wallpaper)
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                                else
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Image(
                                    painter = painterResource(wallpaper),
                                    contentDescription = "Обои ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
                rowIndex += 2
            }
        }
        // Кнопки
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error),
                onClick = {
                    inputIp = appDataStorage.ipAddress
                    inputPort = appDataStorage.port
                    selectedWallpaper = appDataStorage.wallpaperId
                }) {
                Text("Сбросить")
            }
            Spacer(Modifier.width(16.dp))
            if (onSaved != null) {
                Button(onClick = { onSaved.invoke() }) {
                    Text("Закрыть")
                }
                Spacer(Modifier.width(16.dp))
            }
            Button(
                enabled = !hasErrors,
                onClick = {
                    appDataStorage.port = inputPort
                    appDataStorage.ipAddress = inputIp
                    appDataStorage.wallpaperId = selectedWallpaper
                    onSaved?.invoke()
                }) {
                Text("Сохранить")
            }
        }
    }
}

private fun isValidIP(ip: String): Boolean {
    val ipv4Regex = """^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$""".toRegex()
    return ipv4Regex.matches(ip)
}

private fun isValidPort(port: String): Boolean {
    val portRegex = Regex("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")
    return portRegex.matches(port)
}