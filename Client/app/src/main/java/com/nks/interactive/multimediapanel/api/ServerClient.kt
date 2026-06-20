package com.nks.interactive.multimediapanel.api

import com.google.gson.GsonBuilder
import com.nks.interactive.multimediapanel.api.commonData.CommonSseClient
import com.nks.interactive.multimediapanel.api.health.HealthCheckApiContract
import com.nks.interactive.multimediapanel.api.job.JobSseClient
import com.nks.interactive.multimediapanel.api.music.MusicApiContract
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import com.nks.interactive.multimediapanel.api.transport.TransportInfoApiContract
import com.nks.interactive.multimediapanel.api.transport.TransportInfoSseClient
import com.nks.interactive.multimediapanel.gson.LocalDateTimeAdapter
import com.nks.interactive.multimediapanel.gson.LocalTimeAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class ServerClient(ipAddress: String, port: String) {
    private val fullBaseUrl =
        if (port.isEmpty()) "http://$ipAddress/"
        else "http://$ipAddress:$port/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(fullBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val musicApi: MusicApiContract by lazy {
        retrofit.create(MusicApiContract::class.java)
    }

    val healthApi: HealthCheckApiContract by lazy{
        retrofit.create(HealthCheckApiContract::class.java)
    }

    val transportInfoApi: TransportInfoApiContract by lazy{
        retrofit.create(TransportInfoApiContract::class.java)
    }

    val musicSse: MusicSseClient by lazy {
        MusicSseClient(fullBaseUrl.trimEnd('/'))
    }

    val commonSse: CommonSseClient by lazy {
        CommonSseClient(fullBaseUrl.trimEnd('/'), gson)
    }

    val jobSse: JobSseClient by lazy {
        JobSseClient(fullBaseUrl.trimEnd('/'), gson)
    }

    val transportInfoSse:TransportInfoSseClient by lazy {
        TransportInfoSseClient(fullBaseUrl.trimEnd('/'), gson)
    }
}