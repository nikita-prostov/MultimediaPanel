package com.nks.interactive.multimediapanel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class MenuItem(
    val icon: Int,
    val title: String
)

@Composable
fun VerticalMenu(modifier: Modifier = Modifier,items: List<MenuItem>, onItemChanged: (Int) -> Unit = {}, showTitle: Boolean = false){
    var currentIndex by remember{ mutableIntStateOf(0) }

    LazyColumn(modifier.fillMaxHeight().fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        items(items.size){ i ->
            VerticalMenuItem(
                menuItem = items[i],
                isSelected = currentIndex == i,
                showTitle = showTitle,
                onClick ={
                    currentIndex = i
                    onItemChanged(i)
                }
            )
        }
    }
}

@Composable
private fun VerticalMenuItem(menuItem: MenuItem,isSelected: Boolean, onClick: () -> Unit, showTitle: Boolean){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .offset(x=if(isSelected)12.dp else 0.dp),
        shape = RectangleShape,
        colors =
            if(!isSelected)
                CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    containerColor = MaterialTheme.colorScheme.background)
            else
                CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
            Icon(
                modifier = Modifier.width(48.dp).height(48.dp),
                painter = painterResource(menuItem.icon),
                contentDescription = null)
            if(showTitle){
                Spacer(Modifier.width(16.dp))
                Text(menuItem.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}