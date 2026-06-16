package com.nks.interactive.multimediapanel.ui.screens.musicPlayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.music.TracksSource
import com.nks.interactive.multimediapanel.ui.components.HorizontalMenu
import com.nks.interactive.multimediapanel.ui.components.MenuItem
import com.nks.interactive.multimediapanel.viewModel.MusicPlayerVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun MusicPlayerScreen() {
    val menuItems = mutableListOf<MenuItem>()
    val vm = koinViewModel<MusicPlayerVM>()

    LaunchedEffect(Unit) {
        vm.connect()
    }
    DisposableEffect(Unit) {
        onDispose {
            vm.disconnect()
        }
    }

    menuItems.add(MenuItem(R.drawable.audiotrack,"Главная"))
    menuItems.add(MenuItem(R.drawable.queue_music,"Мои треки"))
    menuItems.add(MenuItem(R.drawable.star_rate,"Рекомендации"))
    menuItems.add(MenuItem(R.drawable.save_alt,"Сохранённые"))
    menuItems.add(MenuItem(R.drawable.manage_search,"Поиск"))

    var currentScreen by remember { mutableIntStateOf(0) }
    Column {
        when (currentScreen) {
            0 -> MainScreen(Modifier.weight(1f).fillMaxWidth(), vm)
            1 -> TrackListScreen(Modifier.weight(1f).fillMaxWidth(), vm, TracksSource.MyMusic)
            2 -> TrackListScreen(Modifier.weight(1f).fillMaxWidth(), vm, TracksSource.Recommendations)
            3 -> TrackListScreen(Modifier.weight(1f).fillMaxWidth(), vm, TracksSource.Local)
            4 -> SearchScreen(Modifier.weight(1f).fillMaxWidth(),vm)
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