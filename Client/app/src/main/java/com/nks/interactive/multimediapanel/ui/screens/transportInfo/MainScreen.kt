package com.nks.interactive.multimediapanel.ui.screens.transportInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.IconText
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.TransportInfoScreenVM

@Composable
fun MainScreen(modifier: Modifier = Modifier, vm: TransportInfoScreenVM) {

    val transportInfo by vm.transportInfo

    Column(modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
        Toolbar("Главная")
        if(transportInfo != null){
            if(transportInfo?.fuelInfo != null){
                Card(Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(Modifier.fillMaxWidth().padding(8.dp)) {
                        IconText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Информация о топливе:",
                            icon = R.drawable.local_gas_station)
                        Text("Средний расход: ${"%.1f".format(transportInfo?.fuelInfo?.averageConsumption ?: 0f)}л/100 км")
                        Text("Запас хода: " + transportInfo?.fuelInfo?.range?.toInt() + " км")
                        Text("Уровень топлива: " + getFuelLevel(transportInfo?.fuelInfo?.max ?: 1000f, transportInfo?.fuelInfo?.current ?: 1000f) + "%")
                        Text("Уровень AdBlue: " + getFuelLevel(transportInfo?.fuelInfo?.adBlueMax ?: 1000f, transportInfo?.fuelInfo?.adBlueLevel ?: 1000f) + "%")
                    }
                }
            }
            Card(Modifier.fillMaxWidth().padding(8.dp)) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    IconText(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Повреждения транспорта:",
                        icon = R.drawable.build)
                    Text("Грузовик: " + transportInfo?.truckDamage?.average + "%")
                    if(transportInfo?.trailerDamage != null)
                        Text("Прицеп: " + transportInfo?.trailerDamage?.average + "%")
                }
            }
            if(transportInfo?.errors?.isNotEmpty() == true){
                Toolbar("Последние ошибки")
                Spacer(Modifier.height(8.dp))
                transportInfo?.errors?.forEachIndexed { i, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(8.dp)) {
                            Text("Код: " + item.code)
                            Text("Описание: " + item.description)
                            Text("Дата: " + item.dateTime.format())
                        }
                    }
                }
            }
        }
    }
}

fun getFuelLevel(max:Float,current:Float):Int{
    val percent = max/100f
    return (current/percent).toInt()
}