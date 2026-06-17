package com.nks.interactive.multimediapanel.api.transport

import com.nks.interactive.multimediapanel.models.transportInfo.FullLogDto
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDateTime

interface TransportInfoApiContract {
    @GET("transport/logs")
    suspend fun getLogs(
        @Query("from") from: LocalDateTime? = null,
        @Query("to") to: LocalDateTime? = null,
        @Query("activeOnly") activeOnly: Boolean = false
    ): List<FullLogDto>

    @POST("transport/logs/clear")
    suspend fun clearLogs(): ResponseBody
}