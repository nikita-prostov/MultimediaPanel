package com.nks.interactive.multimediapanel.ui.screens.job

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.job.Cargo
import com.nks.interactive.multimediapanel.models.job.Point
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.JobScreenVM
import java.time.LocalDateTime

@Composable
fun CurrentJobScreen(modifier:Modifier = Modifier, vm: JobScreenVM){

    val jobInfo by vm.jobInfo

    Column(modifier) {
        Toolbar("Текущее задание")
        if(jobInfo != null){
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                if(!jobInfo!!.isLoaded){
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.warning),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ожидает загрузки...",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if(jobInfo!!.isLate){
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.access_time),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Опоздание на: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                PointCard(jobInfo!!.source, Modifier.fillMaxWidth(),true)
                Spacer(Modifier.height(8.dp))
                PointCard(jobInfo!!.destination, Modifier.fillMaxWidth(),false)
                Spacer(Modifier.height(8.dp))
                CargoCard(jobInfo!!.cargo,Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                if(jobInfo!!.isLoaded) {
                    if (!jobInfo!!.isLate) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.access_time),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Осталось времени: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.access_time),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Опоздание на: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.straighten),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Расстояние: " + jobInfo!!.planedDistance + "км",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.payments),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Доход: " + jobInfo!!.income + "€",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        else{
            Text(
                text = "Сейчас нет работы",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PointCard(point: Point, modifier: Modifier, isSource: Boolean){
    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Icon(painterResource(if(isSource) R.drawable.north_east else R.drawable.south_west), null)
                Spacer(Modifier.width(8.dp))
                Text(if(isSource) "Отправитель" else "Получатель")
            }
            Row(Modifier.fillMaxWidth()) {
                Icon(painterResource(R.drawable.place), null)
                Spacer(Modifier.width(8.dp))
                Text("Город: ${point.city}")
            }
            Row(Modifier.fillMaxWidth()) {
                Icon(painterResource(R.drawable.apartment), null)
                Spacer(Modifier.width(8.dp))
                Text("Компания: ${point.company}")
            }
        }
    }
}

@Composable
private fun CargoCard(cargo: Cargo, modifier: Modifier){
    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.inventory_2), null)
                Spacer(Modifier.width(8.dp))
                Text("Груз")
            }
            Text("Наименование: " + cargo.name)
            Text("Масса: " + cargo.mass/1000 + " тонн")
            Text("Повреждения: " + cargo.damage + "%")
        }
    }
}

fun LocalDateTime.toDurationHoursAndMinutes(): String {
    // Вычисляем общее количество часов и минут от начала эпохи (0001-01-01T00:00)
    val totalHours = (dayOfYear - 1) * 24 + hour
    val totalMinutes = minute
    return "${totalHours}ч ${totalMinutes}м"
}