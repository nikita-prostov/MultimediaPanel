package com.nks.interactive.multimediapanel.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.MenuItem
import com.nks.interactive.multimediapanel.ui.components.VerticalMenu
import com.nks.interactive.multimediapanel.ui.screens.home.HomeScreen
import com.nks.interactive.multimediapanel.ui.screens.settings.SettingsScreen

@Composable
fun MainScreen(modifier: Modifier){
    var menuItems = mutableListOf<MenuItem>()
    menuItems.add(MenuItem(R.drawable.home,"Главная"))             // 0
    menuItems.add(MenuItem(R.drawable.queue_music,"Музыка"))       // 1
    menuItems.add(MenuItem(R.drawable.assignment,"Задание"))       // 2
    menuItems.add(MenuItem(R.drawable.local_shipping,"Транспорт")) // 3
    menuItems.add(MenuItem(R.drawable.list,"События"))             // 4
    menuItems.add(MenuItem(R.drawable.settings,"Настройки"))       // 5

    var currentScreen by remember { mutableIntStateOf(0) }

    Row(modifier.fillMaxSize()) {
        VerticalMenu(Modifier.width(64.dp), menuItems, onItemChanged = {currentScreen = it})
        Box(Modifier.weight(1f)){
            when(currentScreen){
                0 -> HomeScreen()
                5 -> SettingsScreen()
            }
        }
    }
}