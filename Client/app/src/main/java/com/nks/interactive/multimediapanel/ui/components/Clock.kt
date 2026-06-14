package com.nks.interactive.multimediapanel.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun Clock(dateTime: LocalDateTime, showData: Boolean = true) {
    OutlinedCard(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
        contentColor = MaterialTheme.colorScheme.onBackground
    )) {
        Column(Modifier.padding(12.dp)) {
            Text(
                "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.headlineLarge
            )
            if (showData) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "${dateTime.dayOfMonth.toString().padStart(2, '0')}.${dateTime.monthValue.toString().padStart(2, '0')}.${dateTime.year}"
                )
            }
        }
    }
}