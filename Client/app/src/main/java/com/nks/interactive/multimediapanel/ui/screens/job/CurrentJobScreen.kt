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
import com.nks.interactive.multimediapanel.ui.components.IconText
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
                    IconText(
                        modifier = Modifier.fillMaxWidth(),
                        text =  "Ожидает загрузки...",
                        icon = R.drawable.warning
                    )
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
                        IconText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Опопздание на: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                            icon = R.drawable.access_time
                        )
                    }
                }
                PointCard(jobInfo!!.source, Modifier.fillMaxWidth(),true)
                Spacer(Modifier.height(8.dp))
                PointCard(jobInfo!!.destination, Modifier.fillMaxWidth(),false)
                Spacer(Modifier.height(8.dp))
                CargoCard(jobInfo!!.cargo,Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                if (!jobInfo!!.isLate) {
                    IconText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Осталось времени: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                        icon = R.drawable.access_time
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        IconText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Опопздание на: " + jobInfo!!.remainingDeliveryTime.toDurationHoursAndMinutes(),
                            icon = R.drawable.access_time
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                IconText(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Расстояние: " + jobInfo!!.planedDistance + "км",
                    icon = R.drawable.sports_score
                )
                Spacer(Modifier.height(8.dp))
                IconText(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Доход: " + jobInfo!!.income + "€",
                    icon = R.drawable.payments
                )
                Spacer(Modifier.height(8.dp))
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
            IconText(
                modifier = Modifier.fillMaxWidth(),
                text = if(isSource) "Отправитель" else "Получатель",
                icon = if(isSource) R.drawable.north_east else R.drawable.south_west
            )
            IconText(
                modifier = Modifier.fillMaxWidth(),
                text = "Город: ${point.city}",
                icon = R.drawable.place
            )
            IconText(
                modifier = Modifier.fillMaxWidth(),
                text = "Компания: ${point.company}",
                icon = R.drawable.apartment
            )
        }
    }
}

@Composable
private fun CargoCard(cargo: Cargo, modifier: Modifier){
    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            IconText(
                modifier = Modifier.fillMaxWidth(),
                text = "Груз",
                icon = R.drawable.inventory_2)
            Text("Наименование: " + cargo.name)
            Text("Масса: " + cargo.mass/1000 + " тонн")
            Text("Повреждения: " + cargo.damage + "%")
        }
    }
}

fun LocalDateTime.toDurationHoursAndMinutes(): String {
    val totalHours = (dayOfYear - 1) * 24 + hour
    val totalMinutes = minute
    return "${totalHours}ч ${totalMinutes}м"
}