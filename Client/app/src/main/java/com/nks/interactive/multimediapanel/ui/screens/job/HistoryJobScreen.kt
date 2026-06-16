package com.nks.interactive.multimediapanel.ui.screens.job

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.JobScreenVM

@Composable
fun HistoryJobScreen(modifier:Modifier = Modifier, vm: JobScreenVM) {

    LaunchedEffect(Unit) {
        vm.getHistory()
    }
    val history by vm.history
    Column(modifier) {
        Toolbar("История заказов")
        if(history.isNotEmpty()){
            LazyColumn(Modifier.fillMaxWidth()) {
                items(history.size){
                    val job = history[it]
                    Card(Modifier.fillMaxWidth()){
                        Column(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.north_east), null)
                                Spacer(Modifier.width(8.dp))
                                Text("Отправитель: " + job.source.city +" - " + job.source.company)
                            }
                            Row(Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.south_west), null)
                                Spacer(Modifier.width(8.dp))
                                Text("Получатель: " + job.destination.city +" - " + job.destination.company)
                            }
                            Row(Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.inventory_2), null)
                                Spacer(Modifier.width(8.dp))
                                Text("Груз: " + job.cargo.name)
                            }
                            Row(Modifier.fillMaxWidth()) {
                                Icon(painterResource(R.drawable.payments), null)
                                Spacer(Modifier.width(8.dp))
                                Text("Доход: " + job.income)
                            }
                        }
                    }
                }
            }
        }
    }
}