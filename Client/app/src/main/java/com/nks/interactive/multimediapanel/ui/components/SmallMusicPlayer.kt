package com.nks.interactive.multimediapanel.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.music.AudioTrack
import com.nks.interactive.multimediapanel.models.music.PlayerState
import com.nks.interactive.multimediapanel.models.music.RepeatMode
import com.nks.interactive.multimediapanel.models.music.TracksSource
import java.time.LocalTime

@Composable
fun SmallMusicPlayer(
    playerState: PlayerState,
    baseUrl: String,
    onPlayClicked: ()-> Unit = {},
    onPauseClicked:() -> Unit = {},
    onPlayNextClicked:() -> Unit = {},
    onPlayPrevClicked:() -> Unit = {},
    onAddClicked:() -> Unit = {},
    onRemoveClicked:() -> Unit = {},
    onShuffleClicked:() -> Unit = {},
    onSortClicked:() -> Unit = {},
    onRepeatModeChanged:(RepeatMode) -> Unit = {}
) {
    val track = playerState.track ?: return
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.25f),
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(96.dp).background(MaterialTheme.colorScheme.primary)){
                Icon(
                    painter = painterResource(R.drawable.audiotrack),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize())
                AsyncImage(
                    clipToBounds = true,
                    contentDescription = null,
                    model = "${baseUrl}music/thumb?albumId=${track.albumId}",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(Modifier.weight(1f).padding(8.dp)) {
                Text("${track.title} - ${track.artist}")
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Button(
                        onClick = if(playerState.isShuffled) onSortClicked else onShuffleClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(playerState.isShuffled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            contentColor = if(playerState.isShuffled) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painterResource(R.drawable.shuffle), null)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onPlayPrevClicked) {
                        Icon(painterResource(R.drawable.skip_previous),null)
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick =
                        {
                            if(!playerState.isPlaying && !playerState.isLoading)
                                onPlayClicked()
                            else if(!playerState.isLoading)
                                onPauseClicked()
                        })
                    {
                        Icon(painterResource(
                            if(!playerState.isPlaying && !playerState.isLoading) R.drawable.play_arrow
                        else if(!playerState.isLoading) R.drawable.pause
                        else R.drawable.autorenew),null)
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = onPlayNextClicked) {
                        Icon(painterResource(R.drawable.skip_next),null)
                    }
                    if(playerState.source == TracksSource.Recommendations){
                        Spacer(Modifier.width(16.dp))
                        Button(
                            onClick =
                                if(playerState.track.isAdded) onRemoveClicked
                                else onAddClicked
                        ) {
                            Icon(painterResource(if(playerState.track.isAdded)R.drawable.delete else R.drawable.add),null)
                        }
                    }
                    else{
                        Spacer(Modifier.width(16.dp))
                        Button(onClick = onRemoveClicked) {
                            Icon(painterResource(R.drawable.delete),null)
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            when (playerState.repeatMode) {
                                RepeatMode.None -> onRepeatModeChanged(RepeatMode.PlayNext)
                                RepeatMode.PlayNext -> onRepeatModeChanged(RepeatMode.RepeatCurrent)
                                RepeatMode.RepeatCurrent -> onRepeatModeChanged(RepeatMode.None)
                            } },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (playerState.repeatMode) {
                                RepeatMode.None -> MaterialTheme.colorScheme.background
                                RepeatMode.PlayNext -> MaterialTheme.colorScheme.primary
                                RepeatMode.RepeatCurrent -> MaterialTheme.colorScheme.primary
                                },
                            contentColor = when (playerState.repeatMode) {
                                RepeatMode.None -> MaterialTheme.colorScheme.primary
                                RepeatMode.PlayNext -> MaterialTheme.colorScheme.background
                                RepeatMode.RepeatCurrent -> MaterialTheme.colorScheme.background
                            }
                        )
                    ) {
                        Icon(painterResource(if(playerState.repeatMode != RepeatMode.RepeatCurrent) R.drawable.repeat else R.drawable.repeat_one),null)
                    }
                }
            }
        }
    }
}