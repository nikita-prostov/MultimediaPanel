package com.nks.interactive.multimediapanel.musicplayer.models

data class AudioTrack(
    val id: Long,
    val ownerId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId:Long,
    val duration: Int,
    val isAdded: Boolean
)
