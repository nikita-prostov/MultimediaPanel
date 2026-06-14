package com.nks.interactive.multimediapanel.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.api.ServerClient
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.ui.components.Clock
import com.nks.interactive.multimediapanel.ui.components.SmallMusicPlayer
import com.nks.interactive.multimediapanel.viewModel.HomeScreenVM
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun HomeScreen() {
    val appSetting = koinInject<AppDataStorage>()
    val vm = koinViewModel<HomeScreenVM>()
    val playerState by vm.playerState

    val realDateTime = LocalDateTime.now()
    val gameDateTime = LocalDateTime.of(2026,1,12,12,5)
    val nextRestStopAfter = LocalTime.of(8,30)

    LaunchedEffect(Unit) {
        vm.connect()
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.disconnect()
        }
    }

    Box{
        Image(
            painter = painterResource(appSetting.wallpaperId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Clock(realDateTime)
                Clock(gameDateTime)
            }
            val hoursUntilRest = nextRestStopAfter.hour + (nextRestStopAfter.minute / 60.0)

            val (containerColor, contentColor, outlineColor) = when {
                hoursUntilRest <= 2 -> Triple(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),  // Красный с прозрачностью 25%
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.onError
                )
                hoursUntilRest <= 4 -> Triple(
                    Color(0x40FFA000),
                    Color(0xFFFFA000),
                    Color(0xFF6B4600)
                )
                else -> Triple(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
                    MaterialTheme.colorScheme.onBackground,
                    MaterialTheme.colorScheme.outlineVariant
                )
            }

            OutlinedCard(colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ), border = BorderStroke(1.dp,outlineColor)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.hotel),
                        null,
                        Modifier
                            .width(26.dp)
                            .height(26.dp),
                        tint = contentColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Остановка для сна через: ", color = contentColor)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${nextRestStopAfter.hour} ${when(nextRestStopAfter.hour){
                            1 -> "час"
                            2,3,4 -> "часа"
                            else -> "часов"
                        }} ${nextRestStopAfter.minute} минут",
                        color = contentColor
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            playerState?.let {
                SmallMusicPlayer(
                    playerState = it,
                    baseUrl = appSetting.fullBaseUrl,
                    onPlayClicked = {vm.play()},
                    onPauseClicked = {vm.pause()},
                    onPlayNextClicked = {vm.next()},
                    onPlayPrevClicked = {vm.prev()},
                    onAddClicked = {vm.add()},
                    onRemoveClicked = {vm.delete()},
                    onShuffleClicked = {vm.shuffle()},
                    onSortClicked = {vm.sort()},
                    onRepeatModeChanged = {it -> vm.setRepeatMode(it)}
                )
            }
        }
    }
}