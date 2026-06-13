package com.nks.interactive.multimediapanel.ui.screens.settings

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(onSaved: (() -> Unit)? = null) {
    val appDataStorage = koinInject<AppDataStorage>()

    var inputIp by remember { mutableStateOf(appDataStorage.ipAddress) }
    var inputPort by remember { mutableStateOf(appDataStorage.port) }
    val hasErrors = inputIp.isEmpty() || !isValidIP(inputIp) || inputPort.isEmpty() || !isValidPort(inputPort)

    Column(Modifier.fillMaxSize()){
        Toolbar("Настройки")
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputIp,
                maxLines = 1,
                isError = inputIp.isNotEmpty() && !isValidIP(inputIp),
                onValueChange = { inputIp = it},
                label = {
                    Text("Введите ip адрес")
                },
                placeholder = {
                    Text("192.137.1.100")
                },
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = inputPort,
                maxLines = 1,
                isError = inputPort.isNotEmpty() && !isValidPort(inputPort),
                onValueChange = { inputPort = it;},
                label = {
                    Text("Введите порт")
                },
                placeholder = {
                    Text("5110")
                }
            )
        }
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error),
                onClick = {
                    inputIp = appDataStorage.ipAddress
                    inputPort = appDataStorage.port
                }) {
                Text("Сбросить")
            }
            Spacer(Modifier.width(16.dp))
            if(onSaved != null){
                Button(
                    enabled = !hasErrors,
                    onClick = {
                        onSaved.invoke()
                    }) {
                    Text("Закрыть")
                }
                Spacer(Modifier.width(16.dp))
            }
            Button(
                enabled = !hasErrors,
                onClick = {
                    appDataStorage.port = inputPort
                    appDataStorage.ipAddress = inputIp
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