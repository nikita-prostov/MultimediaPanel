package com.nks.interactive.multimediapanel.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage

class MainActivityVM(
    appDataStorage: AppDataStorage
) : ViewModel() {
    var showSettings = mutableStateOf(appDataStorage.ipAddress.isEmpty() || appDataStorage.port.isEmpty())
}