package com.nks.interactive.multimediapanel.models.job

import com.nks.interactive.multimediapanel.models.job.Cargo
import com.nks.interactive.multimediapanel.models.job.Point
import java.time.LocalDateTime

data class CurrentJobInfo(
    val deliveryTime: LocalDateTime,
    val planedDistance: Int,
    val isLate: Boolean,
    val isLoaded: Boolean,
    val remainingDeliveryTime: LocalDateTime,
    val income: Long,
    val source: Point,
    val destination: Point,
    val cargo: Cargo
)