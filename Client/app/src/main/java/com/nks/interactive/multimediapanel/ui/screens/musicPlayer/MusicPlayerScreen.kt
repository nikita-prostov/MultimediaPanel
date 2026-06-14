package com.nks.interactive.multimediapanel.ui.screens.musicPlayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.HorizontalMenu
import com.nks.interactive.multimediapanel.ui.components.MenuItem

@Composable
fun MusicPlayerScreen() {
    val menuItems = mutableListOf<MenuItem>()

    menuItems.add(MenuItem(R.drawable.audiotrack,"Главная"))
    menuItems.add(MenuItem(R.drawable.queue_music,"Мои треки"))
    menuItems.add(MenuItem(R.drawable.star_rate,"Рекомендации"))
    menuItems.add(MenuItem(R.drawable.save_alt,"Сохранённые"))
    menuItems.add(MenuItem(R.drawable.manage_search,"Поиск"))

    var currentScreen by remember { mutableIntStateOf(0) }
    Column {
        Spacer(Modifier.weight(1f))
        HorizontalMenu(
            items = menuItems,
            onItemChanged = {currentScreen = it},
            showTitle = true
        )
    }
}