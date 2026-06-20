package com.nks.interactive.multimediapanel.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.notification.NotificationApiContract
import com.nks.interactive.multimediapanel.models.notification.NotificationDto
import kotlinx.coroutines.launch

class NotificationScreenVM(private val api: NotificationApiContract) : ViewModel() {
    var list = mutableStateOf<List<NotificationDto>>(emptyList())

    fun getAll(page:Int = 1){
        viewModelScope.launch {
            if(page == 1) list.value = emptyList()

            val old = list.value.toMutableList()
            old.addAll(api.getAll(page))
        }
    }
}