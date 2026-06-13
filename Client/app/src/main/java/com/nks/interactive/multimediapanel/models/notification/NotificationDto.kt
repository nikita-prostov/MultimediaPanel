package com.nks.interactive.multimediapanel.models.notification

import java.time.LocalDateTime

data class NotificationDto(
    val title: String?,
    val subTitle: String?,
    val type: NotificationType,
    val amount: Long,
    val dateTime: LocalDateTime
)

enum class NotificationType {
    None,
    Ferry,
    Fined,
    Tollgate,
    Train
}
