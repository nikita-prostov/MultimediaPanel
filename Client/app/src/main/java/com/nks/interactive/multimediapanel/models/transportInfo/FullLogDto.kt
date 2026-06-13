package com.nks.interactive.multimediapanel.models.transportInfo

import java.time.LocalDateTime

data class FullLogDto(
    val code: String = "",
    val isActive: Boolean = false,
    val description: String = "",
    val activeDateTime: LocalDateTime,
    val inactiveDateTime: LocalDateTime? = null
)