package com.nks.interactive.multimediapanel.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.notification.NotificationType
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.ui.screens.transportInfo.format
import com.nks.interactive.multimediapanel.viewModel.NotificationScreenVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationScreen() {
    val vm = koinViewModel<NotificationScreenVM>()
    val list by vm.list
    LaunchedEffect(Unit) {
        vm.getAll(1)
    }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Toolbar("Уведомления")
        Spacer(Modifier.height(8.dp))
        LazyColumn() {
            items(list.size){
                val notification = list[it]

                val (containerColor, contentColor, iconTint) = when (notification.type) {
                    NotificationType.Tollgate -> Triple(
                        Color(0xFFFFF3CD),
                        Color(0xFF664D03),
                        Color(0xFFE6A800)
                    )
                    NotificationType.Fined -> Triple(
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.onErrorContainer,
                        MaterialTheme.colorScheme.error
                    )
                    else -> Triple(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = containerColor,
                        contentColor = contentColor),
                    shape = RectangleShape
                ){
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(
                                    when (notification.type) {
                                        NotificationType.Tollgate -> R.drawable.warning
                                        NotificationType.Fined -> R.drawable.critical
                                        else -> R.drawable.info
                                    }
                                ),
                                modifier = Modifier.width(32.dp).height(32.dp),
                                contentDescription = null,
                                tint = iconTint
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = notification.title ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = notification.subTitle ?: "",
                            color = contentColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Стоимость: " + notification.amount,
                            color = contentColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Дата: " + notification.dateTime.format(),
                            color = contentColor
                        )
                    }
                }
                Spacer(Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant))
            }
        }
    }
}