package com.nks.interactive.multimediapanel.models.transportInfo

import java.time.LocalDateTime

data class ErrorDto(
    val code: String = "",
    val dateTime: LocalDateTime,
    val description: String = ""
)