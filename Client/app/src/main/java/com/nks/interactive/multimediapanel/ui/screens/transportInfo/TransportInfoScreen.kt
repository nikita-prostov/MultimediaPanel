package com.nks.interactive.multimediapanel.ui.screens.transportInfo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.HorizontalMenu
import com.nks.interactive.multimediapanel.ui.components.MenuItem
import com.nks.interactive.multimediapanel.viewModel.TransportInfoScreenVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransportInfoScreen() {
    val menuItems = mutableListOf<MenuItem>()
    val vm = koinViewModel<TransportInfoScreenVM>()
    var currentScreen by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        vm.connect()
    }
    DisposableEffect(Unit) {
        onDispose {
            vm.disconnect()
        }
    }

    menuItems.add(MenuItem(R.drawable.local_shipping,"Главная"))
    menuItems.add(MenuItem(R.drawable.warning,"Повреждения"))
    menuItems.add(MenuItem(R.drawable.list,"Ошибки"))

    Column {
        when (currentScreen) {
            0 -> MainScreen(Modifier.weight(1f).padding(start = 24.dp), vm)
            1 -> DamageScreen(Modifier.weight(1f).padding(start = 24.dp), vm)
            2 -> LogScreen(Modifier.weight(1f).padding(start = 24.dp), vm)
        }
        HorizontalMenu(
            items = menuItems,
            onItemChanged =
                {
                    currentScreen = it
                },
            showTitle = true
        )
    }
}