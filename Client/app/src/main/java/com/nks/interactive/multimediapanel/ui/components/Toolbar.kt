package com.nks.interactive.multimediapanel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun Toolbar(text:String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondary)) {
        Text(
            modifier = Modifier.padding(16.dp,8.dp,8.dp,8.dp),
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
    }
}