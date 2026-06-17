package com.nks.interactive.multimediapanel.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R

@Composable
fun IconText(modifier: Modifier = Modifier, text:String, @DrawableRes icon: Int, color: Color = MaterialTheme.colorScheme.onBackground) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = color
        )
    }
}