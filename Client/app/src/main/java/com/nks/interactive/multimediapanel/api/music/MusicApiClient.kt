package com.nks.interactive.multimediapanel.api.music

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MusicApiClient(ipAddress: String, port: String) {
    private val fullBaseUrl = "http://$ipAddress:$port"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    val api: MusicApiContract by lazy {
        Retrofit.Builder()
            .baseUrl(fullBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiContract::class.java)
    }

    val sse: MusicSseClient by lazy {
        MusicSseClient(fullBaseUrl.trimEnd('/'))
    }
}