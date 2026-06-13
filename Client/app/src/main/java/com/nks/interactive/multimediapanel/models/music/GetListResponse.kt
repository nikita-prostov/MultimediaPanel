package com.nks.interactive.multimediapanel.models.music

data class GetListResponse(
    val tracks:List<AudioTrack>,
    val page: Int,
    val totalPages: Int
)