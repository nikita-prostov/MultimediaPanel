package com.nks.interactive.multimediapanel.ui.screens.musicPlayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.models.music.AudioTrack
import com.nks.interactive.multimediapanel.ui.components.Toolbar
import com.nks.interactive.multimediapanel.viewModel.MusicPlayerVM

@Composable
fun SearchScreen(modifier: Modifier = Modifier, vm: MusicPlayerVM) {
    var queryString by remember { mutableStateOf("") }
    var startSearch by remember { mutableStateOf(false) }
    var playerState by vm.playerState
    var isLoading by vm.isLoading
    var tracks by vm.searchResult

    Column(modifier) {
        Toolbar("Поиск")
        Spacer(Modifier.height(8.dp))
        OutlinedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    modifier = Modifier.weight(1f).padding(0.dp),  // ← Уменьшаем высоту
                    value = queryString,
                    onValueChange = { queryString = it },
                    maxLines = 1,
                    shape = RectangleShape,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = Color.Transparent,  // ← Убираем линию (не в фокусе)
                        focusedIndicatorColor = Color.Transparent,    // ← Убираем линию (в фокусе)
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    startSearch = true
                    vm.search(queryString)
                }) {
                    Text("Найти")
                }
            }
        }
        if(!isLoading){
            Spacer(Modifier.height(8.dp))
            if(tracks.isNotEmpty()){
                LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    items(tracks.size){
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable(onClick = { vm.play(tracks[it]) }),
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
            else if(startSearch){
                Text(
                    text = "Ничего не найдено",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground)
            }
        }
        else{
            Box(Modifier.weight(1f)){
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}