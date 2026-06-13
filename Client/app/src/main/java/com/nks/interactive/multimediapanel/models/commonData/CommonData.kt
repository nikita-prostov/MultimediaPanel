package com.nks.interactive.multimediapanel.models.commonData

import java.time.LocalDateTime
import java.time.LocalTime

data class CommonData(
    val nextRestStopAfter: LocalTime,
    val nextRestStopTime: LocalDateTime,
    val currentGameTime: LocalDateTime
)