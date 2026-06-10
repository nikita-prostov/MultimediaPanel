package com.nks.interactive.multimediapanel.musicplayer.models

data class GetListResponse(
    val tracks:List<AudioTrack>,
    val page: Int,
    val totalPages: Int
)
