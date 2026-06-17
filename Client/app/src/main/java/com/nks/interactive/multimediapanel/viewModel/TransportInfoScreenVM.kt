package com.nks.interactive.multimediapanel.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.transport.TransportInfoApiContract
import com.nks.interactive.multimediapanel.api.transport.TransportInfoSseClient
import com.nks.interactive.multimediapanel.models.transportInfo.ErrorDto
import com.nks.interactive.multimediapanel.models.transportInfo.FullLogDto
import com.nks.interactive.multimediapanel.models.transportInfo.TransportInfo
import kotlinx.coroutines.launch

class TransportInfoScreenVM(private val sseClient: TransportInfoSseClient, private val api: TransportInfoApiContract) : ViewModel(){

    var transportInfo = mutableStateOf<TransportInfo?>(null)
    var errors = mutableStateOf<List<FullLogDto>>(emptyList())
    var isLoading = mutableStateOf(false)

    fun connect(){
        viewModelScope.launch {
            sseClient.transportInfo.collect {
                transportInfo.value = it
            }
        }
    }

    fun getErrors(){
        isLoading.value = true
        viewModelScope.launch {
            errors.value = api.getLogs()
            isLoading.value = false
        }
    }

    fun clearLogs(){
        isLoading.value = true
        viewModelScope.launch {
            api.clearLogs()
            getErrors()
            isLoading.value = false
        }
    }

    fun disconnect(){
        sseClient.disconnect()
    }
}