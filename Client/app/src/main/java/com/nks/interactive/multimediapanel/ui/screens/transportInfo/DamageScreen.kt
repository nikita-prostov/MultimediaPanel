package com.nks.interactive.multimediapanel.ui.screens.transportInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.viewModel.TransportInfoScreenVM

@Composable
fun DamageScreen(modifier: Modifier = Modifier, vm: TransportInfoScreenVM) {

    val transportInfo by vm.transportInfo

    Column(modifier) {
        Card(Modifier.fillMaxWidth().padding(8.dp)) {
            Column(Modifier.fillMaxWidth().padding(8.dp)) {
                Text(
                    text = "Грузовик: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()){
                    DamageIndicator(transportInfo?.truckDamage?.engine ?: 0, "Двигатель")
                    DamageIndicator(transportInfo?.truckDamage?.transmission ?: 0, "Трансмиссия")
                    DamageIndicator(transportInfo?.truckDamage?.cabin ?: 0, "Кабина")
                    DamageIndicator(transportInfo?.truckDamage?.chassis ?: 0, "Шасси")
                    DamageIndicator(transportInfo?.truckDamage?.wheelsAvg ?: 0, "Колёса")
                }
            }
        }
        if(transportInfo?.trailerDamage != null){
            Card(Modifier.fillMaxWidth().padding(8.dp)) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Прицеп: ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()){
                        DamageIndicator(transportInfo?.trailerDamage?.body ?: 0, "Кузов")
                        DamageIndicator(transportInfo?.trailerDamage?.chassis ?: 0, "Шасси")
                        DamageIndicator(transportInfo?.trailerDamage?.wheels ?: 0, "Колёса")
                    }
                }
            }
        }
    }
}

@Composable
private fun DamageIndicator(damage:Int, title: String){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box{
            CircularProgressIndicator(
                progress = damage/100f,
                modifier = Modifier.size(64.dp),
                trackColor = MaterialTheme.colorScheme.onBackground)
            Text("${damage}%", modifier = Modifier.align(Alignment.Center))
        }
        Text(title)
    }
}