package com.nks.interactive.multimediapanel.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalMenu(
    modifier: Modifier = Modifier,
    items: List<MenuItem>,
    onItemChanged: (Int) -> Unit = {},
    showTitle: Boolean = false
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Row(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { i, item ->
            HorizontalMenuItem(
                menuItem = item,
                isSelected = currentIndex == i,
                onClick = {
                    currentIndex = i
                    onItemChanged(i)
                },
                showTitle = showTitle,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HorizontalMenuItem(
    menuItem: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    showTitle: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .offset(y = if (isSelected) (-8).dp else 0.dp),
        shape = RectangleShape,
        colors = if (!isSelected)
            CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.background
            )
        else
            CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.secondary
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp, 4.dp).fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier.width(32.dp).height(32.dp),
                painter = painterResource(menuItem.icon),
                contentDescription = null
            )
            if (showTitle) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = menuItem.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}