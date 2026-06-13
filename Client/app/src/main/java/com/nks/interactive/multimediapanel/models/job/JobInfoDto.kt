package com.nks.interactive.multimediapanel.models.job

import com.nks.interactive.multimediapanel.models.job.Point
import java.time.LocalDateTime

data class JobInfoDto(
    val acceptedAt: LocalDateTime,
    val finishedAt: LocalDateTime? = null,
    val distance: Float,
    val isLate: Boolean,
    val plannedIncome: Long,
    val income: Long,
    val source: Point,
    val destination: Point,
    val cargo: Cargo,
    val isDeliveried: Boolean,
    val penalty: Long
)