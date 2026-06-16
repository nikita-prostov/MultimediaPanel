package com.nks.interactive.multimediapanel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Toolbar(text:String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        Text(
            modifier = Modifier.padding(16.dp,8.dp,8.dp,8.dp),
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
    }
}