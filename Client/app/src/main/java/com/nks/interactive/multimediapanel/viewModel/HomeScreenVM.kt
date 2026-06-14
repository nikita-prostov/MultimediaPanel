package com.nks.interactive.multimediapanel.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.commonData.CommonSseClient
import com.nks.interactive.multimediapanel.api.music.MusicApiContract
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import com.nks.interactive.multimediapanel.models.commonData.CommonData
import com.nks.interactive.multimediapanel.models.music.PlayerState
import com.nks.interactive.multimediapanel.models.music.RepeatMode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeScreenVM(
    private val musicSseClient: MusicSseClient,
    private val musicApi: MusicApiContract,
    private val commonSseClient: CommonSseClient,
    appDataStorage: AppDataStorage) : ViewModel() {

    var playerState = mutableStateOf<PlayerState?>(null)
    var commonData = mutableStateOf<CommonData?>(null)

    var wallpaperId = appDataStorage.wallpaperId
    var baseUrl = appDataStorage.fullBaseUrl

    fun connect(){
        viewModelScope.launch {
            musicSseClient.playerState.collect {
                playerState.value = it
            }
        }
        viewModelScope.launch {
            commonSseClient.commonData.collect {
                commonData.value = it
            }
        }
    }

    fun play(){
        if(playerState.value != null){
            if(playerState.value!!.isLoading) return
            if(!playerState.value!!.isPlaying){
                viewModelScope.launch {
                    musicApi.play()
                }
            }
        }
    }

    fun pause(){
        if(playerState.value != null){
            if(playerState.value!!.isLoading) return
            if(playerState.value!!.isPlaying){
                viewModelScope.launch {
                    musicApi.pause()
                }
            }
        }
    }

    fun next(){
        viewModelScope.launch {
            musicApi.next()
        }
    }

    fun prev(){
        viewModelScope.launch {
            musicApi.prev()
        }
    }

    fun add(){
        viewModelScope.launch {
            musicApi.add(playerState.value?.track?.id?:0,playerState.value?.track?.ownerId?:0)
        }
    }

    fun delete(){
        viewModelScope.launch {
            musicApi.delete(playerState.value?.track?.id?:0,playerState.value?.track?.ownerId?:0)
        }
    }

    fun shuffle(){
        viewModelScope.launch {
            musicApi.shuffle()
        }
    }

    fun sort(){
        viewModelScope.launch {
            musicApi.sort()
        }
    }

    fun setRepeatMode(mode: RepeatMode){
        viewModelScope.launch {
            musicApi.setRepeatMode(mode)
        }
    }

    fun disconnect() {
        musicSseClient.disconnect()
        commonSseClient.disconnect()
    }
}