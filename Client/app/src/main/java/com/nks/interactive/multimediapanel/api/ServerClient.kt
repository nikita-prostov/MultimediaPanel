package com.nks.interactive.multimediapanel.api

import com.nks.interactive.multimediapanel.api.commonData.CommonSseClient
import com.nks.interactive.multimediapanel.api.health.HealthCheckApiContract
import com.nks.interactive.multimediapanel.api.job.JobApiContract
import com.nks.interactive.multimediapanel.api.job.JobSseClient
import com.nks.interactive.multimediapanel.api.music.MusicApiContract
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import com.nks.interactive.multimediapanel.api.notification.NotificationApiContract
import com.nks.interactive.multimediapanel.api.notification.NotificationSseClient
import com.nks.interactive.multimediapanel.api.transport.TransportInfoApiContract
import com.nks.interactive.multimediapanel.api.transport.TransportInfoSseClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServerClient(ipAddress: String, port: String) {
    private val fullBaseUrl =
        if (port.isEmpty()) "http://$ipAddress/"
        else "http://$ipAddress:$port/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(fullBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val musicApi: MusicApiContract by lazy {
        retrofit.create(MusicApiContract::class.java)
    }

    val healthApi: HealthCheckApiContract by lazy{
        retrofit.create(HealthCheckApiContract::class.java)
    }

    val jobApi: JobApiContract by lazy{
        retrofit.create(JobApiContract::class.java)
    }

    val notificationApi: NotificationApiContract by lazy{
        retrofit.create(NotificationApiContract::class.java)
    }

    val transportInfoApi: TransportInfoApiContract by lazy{
        retrofit.create(TransportInfoApiContract::class.java)
    }

    val musicSse: MusicSseClient by lazy {
        MusicSseClient(fullBaseUrl.trimEnd('/'))
    }

    val commonSse: CommonSseClient by lazy {
        CommonSseClient(fullBaseUrl.trimEnd('/'))
    }

    val jobSse: JobSseClient by lazy {
        JobSseClient(fullBaseUrl.trimEnd('/'))
    }

    val notificationSse: NotificationSseClient by lazy {
        NotificationSseClient(fullBaseUrl.trimEnd('/'))
    }

    val transportInfoSse:TransportInfoSseClient by lazy {
        TransportInfoSseClient(fullBaseUrl.trimEnd('/'))
    }
}