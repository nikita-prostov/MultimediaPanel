package com.nks.interactive.multimediapanel.models

data class GetListResponse(
    val tracks:List<AudioTrack>,
    val page: Int,
    val totalPages: Int
)