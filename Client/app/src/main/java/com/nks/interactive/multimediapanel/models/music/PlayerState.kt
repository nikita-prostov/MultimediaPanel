package com.nks.interactive.multimediapanel.models.music

data class PlayerState(
    val position: Int,
    var volume:Float,
    val isPlaying: Boolean,
    val isShuffled: Boolean,
    val isLoading: Boolean,
    val track: AudioTrack,
    val repeatMode: RepeatMode,
    val source: TracksSource
)

enum class RepeatMode {
    PlayNext,
    RepeatCurrent,
    None
}

enum class TracksSource{
    MyMusic,
    Recommendations,
    Local
}