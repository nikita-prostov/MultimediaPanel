package com.nks.interactive.multimediapanel.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.notification.NotificationSseClient
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.models.notification.NotificationDto
import kotlinx.coroutines.launch

class MainActivityVM(
    private val sseClient:NotificationSseClient,
    appDataStorage: AppDataStorage
) : ViewModel() {
    val notification = mutableStateOf<NotificationDto?>(null)

    var showSettings = mutableStateOf(appDataStorage.ipAddress.isEmpty() || appDataStorage.port.isEmpty())

    fun connect(){
        viewModelScope.launch {
            sseClient.notification.collect {
                notification.value = it
            }
        }
    }

    fun disconnect(){
        sseClient.disconnect()
    }
}