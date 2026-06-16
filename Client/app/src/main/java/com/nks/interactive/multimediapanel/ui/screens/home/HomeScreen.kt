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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.Clock
import com.nks.interactive.multimediapanel.ui.components.SmallMusicPlayer
import com.nks.interactive.multimediapanel.viewModel.HomeScreenVM
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Composable
fun HomeScreen() {
    val vm = koinViewModel<HomeScreenVM>()
    val playerState by vm.playerState
    val commonData by vm.commonData

    val realDateTime = LocalDateTime.now()

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
            painter = painterResource(vm.wallpaperId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Clock(realDateTime)
                Clock(commonData?.currentGameTime ?: LocalDateTime.now())
            }
            val hoursUntilRest = (commonData?.nextRestStopAfter?.hour ?: 0) + ((commonData?.nextRestStopAfter?.minute?:0)/ 60.0)

            val (containerColor, contentColor, outlineColor) = when {
                hoursUntilRest <= 2 -> Triple(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
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
                        "${commonData?.nextRestStopAfter?.hour ?: 0} ${when(commonData?.nextRestStopAfter?.hour ?: 0){
                            1 -> "час"
                            2,3,4 -> "часа"
                            else -> "часов"
                        }} ${commonData?.nextRestStopAfter?.minute ?: 0} минут",
                        color = contentColor
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            playerState?.let {
                SmallMusicPlayer(
                    playerState = it,
                    baseUrl = vm.baseUrl,
                    onPlayClicked = {vm.play()},
                    onPauseClicked = {vm.pause()},
                    onPlayNextClicked = {vm.next()},
                    onPlayPrevClicked = {vm.prev()},
                    onAddClicked = {vm.add()},
                    onRemoveClicked = {vm.delete()},
                    onShuffleClicked = {vm.shuffle()},
                    onSortClicked = {vm.sort()},
                    onRepeatModeChanged = {mode -> vm.setRepeatMode(mode)}
                )
            }
        }
    }
}