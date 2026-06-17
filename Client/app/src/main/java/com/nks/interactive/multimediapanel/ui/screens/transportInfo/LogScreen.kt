package com.nks.interactive.multimediapanel.ui.screens.transportInfo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.models.transportInfo.FullLogDto
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.TransportInfoScreenVM
import java.time.LocalDateTime

@Composable
fun LogScreen(modifier: Modifier = Modifier, vm: TransportInfoScreenVM) {

    val errors by vm.errors
    val isLoading by vm.isLoading

    LaunchedEffect(Unit) {
        vm.getErrors()
    }

    Column(modifier) {
        Toolbar(text = "Ошибки")
        if(isLoading){
            Box(Modifier.fillMaxSize()){
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        else{
            Box(Modifier.weight(1f).fillMaxWidth().padding(8.dp)){
                if(errors.isNotEmpty()){
                    LazyColumn(Modifier.fillMaxWidth()) {
                        items(errors.size){
                            val log = errors[it]
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = if(log.isActive)
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                else
                                    CardDefaults.cardColors()
                            ) {
                                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                                    Text("Код: " + log.code)
                                    Text("Описание: " + log.description)
                                    Text("Дата возникновения: " + log.activeDateTime.format())
                                    if(log.inactiveDateTime != null)
                                        Text("Дата устранения: " + log.activeDateTime.format())
                                }
                            }
                        }
                    }
                    Button(onClick = {
                        vm.clearLogs()
                    }, modifier = Modifier.align(Alignment.BottomEnd)) {
                        Text("Очистить")
                    }
                }
                else
                    Text(
                        text = "Ошибок нет",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

fun LocalDateTime.format(): String {
    return "${this.dayOfMonth.toString().padStart(2, '0')}.${this.monthValue.toString().padStart(2, '0')}.${this.year} ${this.hour}:${this.minute}"
}