package com.nks.interactive.multimediapanel.ui.screens.job

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nks.interactive.multimediapanel.viewModel.JobScreenVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun JobScreen(modifier: Modifier = Modifier){
    val menuItems = mutableListOf<MenuItem>()
    val vm = koinViewModel<JobScreenVM>()
    var currentScreen by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        vm.connect()
    }
    DisposableEffect(Unit) {
        onDispose {
            vm.disconnect()
        }
    }

    menuItems.add(MenuItem(R.drawable.assignment,"Текущее"))
    menuItems.add(MenuItem(R.drawable.list,"История"))

    Column {
        when (currentScreen) {
            0 -> CurrentJobScreen(Modifier.weight(1f).fillMaxWidth().padding(start = 24.dp),vm)
            1 -> HistoryJobScreen(Modifier.weight(1f).fillMaxWidth().padding(start = 24.dp),vm)
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