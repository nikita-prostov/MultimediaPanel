package com.nks.interactive.multimediapanel.ui.screens.musicPlayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nks.interactive.multimediapanel.models.music.TracksSource
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.MusicPlayerVM

@Composable
fun TrackListScreen(modifier: Modifier, vm: MusicPlayerVM, source: TracksSource = TracksSource.MyMusic) {
    val tracks by vm.tracks
    val playerState by vm.playerState
    val isLoading by vm.isLoading

    LaunchedEffect(Unit) {
        if(playerState?.source != source) vm.load(source)
        vm.getList()
    }

    Column(modifier) {
        Toolbar(when(source){
            TracksSource.MyMusic -> "Моя музыка"
            TracksSource.Recommendations -> "Рекомендации"
            else -> "Сохранённые"
        })
        if(!isLoading){
            LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)  ){
                items(tracks.size){
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable(onClick = { vm.play(it) }),
                        colors = if(playerState?.track?.title == tracks[it].title) CardDefaults.cardColors(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.background
                        )else CardDefaults.cardColors()
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            AsyncImage(
                                clipToBounds = true,
                                contentDescription = null,
                                model = "${vm.baseUrl}music/thumb?albumId=${tracks[it].albumId}",
                                modifier = Modifier.size(72.dp).aspectRatio(1f)
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(tracks[it].title)
                                Text(tracks[it].artist)
                            }
                        }
                    }
                }
            }
        }
        else{
            Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}