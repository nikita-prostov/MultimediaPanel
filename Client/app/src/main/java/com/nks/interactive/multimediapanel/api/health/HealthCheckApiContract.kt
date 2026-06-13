package com.nks.interactive.multimediapanel.api.health

import retrofit2.Response
import retrofit2.http.GET

interface HealthCheckApiContract {
    @GET("health/check")
    suspend fun check():Response<Unit>
}