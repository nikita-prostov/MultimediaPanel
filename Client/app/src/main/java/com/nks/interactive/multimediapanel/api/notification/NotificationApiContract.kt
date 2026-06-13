package com.nks.interactive.multimediapanel.api.notification

import com.nks.interactive.multimediapanel.models.notification.NotificationDto
import com.nks.interactive.multimediapanel.models.notification.NotificationType
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime

interface NotificationApiContract {
    @GET("notifications/list/all")
    suspend fun getAll(@Query("page") page:Int = 1):List<NotificationDto>

    @GET("notifications/list/filtered")
    suspend fun getAllFiltered(
        @Query("page") page: Int = 1,
        @Query("from") from: LocalDateTime? = null,
        @Query("to") to: LocalDateTime? = null,
        @Query("type") type: NotificationType = NotificationType.None,
        @Query("title") title: String? = null
    )
}