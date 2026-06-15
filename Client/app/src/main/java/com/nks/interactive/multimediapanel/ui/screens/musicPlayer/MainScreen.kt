package com.nks.interactive.multimediapanel.ui.screens.musicPlayer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.music.RepeatMode
import com.nks.interactive.multimediapanel.ui.components.VerticalSlider
import com.nks.interactive.multimediapanel.viewModel.MusicPlayerVM
import java.time.LocalTime

@Composable
fun MainScreen(modifier: Modifier = Modifier, vm: MusicPlayerVM) {

    val playerState by vm.playerState
    var volume by remember { mutableFloatStateOf(playerState?.volume ?: 0.1f) }
    var position by remember { mutableFloatStateOf(playerState?.position?.toFloat() ?: 0f) }

    Row(modifier
        .fillMaxHeight()
        .padding(8.dp)) {
        Column(Modifier.weight(1f)) {
            Row(Modifier.padding(12.dp)) {
                Card(Modifier.size(150.dp)){
                    Box(Modifier.fillMaxSize()){
                        Icon(
                            painter = painterResource(R.drawable.audiotrack),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxSize())
                        AsyncImage(
                            clipToBounds = true,
                            contentDescription = null,
                            model = "${vm.baseUrl}music/thumb?albumId=${playerState?.track?.albumId ?: 0}",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = playerState?.track?.title ?: "Undefined",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = playerState?.track?.artist ?: "Undefined",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge)
                    if(playerState?.track?.album != "unnamed")
                        Text(
                            text = "Альбом: " + playerState?.track?.album ?: "Undefined",
                            color = MaterialTheme.colorScheme.onBackground)
                }
            }
            Spacer(Modifier.height(8.dp))
            Slider(
                value = playerState?.position?.toFloat() ?: 0f,
                valueRange = 0f..(playerState?.track?.duration?.toFloat() ?: 1f),
                onValueChange = { position = it },
                onValueChangeFinished = { vm.seekTo(position.toInt()) },
            )

            Row(modifier = Modifier.fillMaxWidth()){
                Text(
                    text = formatDuration(playerState?.position ?: 0),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatDuration(playerState?.track?.duration ?: 0),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()){
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(playerState?.isShuffled == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        contentColor = if(playerState?.isShuffled == true) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        if(playerState?.isShuffled == true) vm.sort()
                        else vm.shuffle()
                }) {
                    Icon(painterResource(R.drawable.shuffle),null, Modifier.size(24.dp))
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = {vm.prev()}) {
                    Icon(painterResource(R.drawable.skip_previous),null, Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = {
                    if(playerState?.isPlaying == false && playerState?.isLoading == false) vm.play()
                    else if(playerState?.isLoading == false) vm.pause()
                }) {
                    Icon(
                        painter = painterResource(
                            if(playerState?.isPlaying == false && playerState?.isLoading == false) R.drawable.play_arrow
                            else if(playerState?.isLoading == false) R.drawable.pause
                            else R.drawable.autorenew),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Button(onClick = {vm.next()}) {
                    Icon(painterResource(R.drawable.skip_next),null, Modifier.size(24.dp))
                }
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    if (playerState?.track?.isAdded == true) vm.delete()
                    else vm.add()
                }) {
                    Icon(
                        painter = painterResource(
                            if (playerState?.track?.isAdded == true) R.drawable.delete
                                else R.drawable.add),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (playerState?.repeatMode ?: RepeatMode.None) {
                            RepeatMode.None -> MaterialTheme.colorScheme.background
                            RepeatMode.PlayNext -> MaterialTheme.colorScheme.primary
                            RepeatMode.RepeatCurrent -> MaterialTheme.colorScheme.primary
                            },
                        contentColor = when (playerState?.repeatMode ?: RepeatMode.None) {
                            RepeatMode.None -> MaterialTheme.colorScheme.primary
                            RepeatMode.PlayNext -> MaterialTheme.colorScheme.background
                            RepeatMode.RepeatCurrent -> MaterialTheme.colorScheme.background
                        }),
                    onClick = {
                        when (playerState?.repeatMode ?: RepeatMode.None) {
                            RepeatMode.None -> vm.setRepeatMode(RepeatMode.PlayNext)
                            RepeatMode.PlayNext -> vm.setRepeatMode(RepeatMode.RepeatCurrent)
                            RepeatMode.RepeatCurrent -> vm.setRepeatMode(RepeatMode.None)
                        }
                    }) {
                    Icon(
                        painter = painterResource(
                            if(playerState?.repeatMode != RepeatMode.RepeatCurrent) R.drawable.repeat
                            else R.drawable.repeat_one),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp))
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.volume_up),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            VerticalSlider(
                value = volume,
                onValueChangeFinished = { vm.setVolume(volume) },
                onValueChange = { volume = it },
                modifier = Modifier.weight(1f))
            Spacer(Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.volume_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}