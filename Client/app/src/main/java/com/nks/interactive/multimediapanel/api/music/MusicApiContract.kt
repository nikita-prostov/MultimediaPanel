package com.nks.interactive.multimediapanel.api.music

import com.nks.interactive.multimediapanel.models.AudioTrack
import com.nks.interactive.multimediapanel.models.GetListResponse
import com.nks.interactive.multimediapanel.models.RepeatMode
import com.nks.interactive.multimediapanel.models.TracksSource
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface MusicApiContract {
    @POST("music/play")
    suspend fun play(@Query("position") position: Int = -1): Response<Unit>

    @POST("music/play/track")
    suspend fun playTrack(
        @Query("audioId") audioId: Long,
        @Query("ownerId") ownerId: Long
    ): Response<Unit>

    @POST("music/pause")
    suspend fun pause(): Response<Unit>

    @POST("music/play/next")
    suspend fun next(): Response<Unit>

    @POST("music/play/prev")
    suspend fun prev(): Response<Unit>

    // ========== Управление ==========
    @POST("music/shuffle")
    suspend fun shuffle(): Response<Unit>

    @POST("music/sort")
    suspend fun sort(): Response<Unit>

    @POST("music/repeat/set")
    suspend fun setRepeatMode(@Query("repeatMode") mode: RepeatMode): Response<Unit>

    @POST("music/volume/set")
    suspend fun setVolume(@Query("value") value: Float): Response<Unit>

    @POST("music/seek/to")
    suspend fun seekTo(@Query("position") position: Int): Response<Unit>

    @GET("music/list")
    suspend fun getList(@Query("page") page: Int = 1): Response<GetListResponse>

    @GET("music/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("ignoreCase") ignoreCase: Boolean = true
    ): Response<List<AudioTrack>>

    @POST("music/load")
    suspend fun load(
        @Query("source") source: TracksSource? = null,
        @Query("page") page: Int = 1
    ): Response<Unit>

    @POST("music/add")
    suspend fun add(
        @Query("audioId") audioId: Long,
        @Query("ownerId") ownerId: Long
    ): Response<Unit>

    @DELETE("music/delete")
    suspend fun delete(
        @Query("audioId") audioId: Long,
        @Query("ownerId") ownerId: Long
    ): Response<Unit>

    @GET("music/thumb")
    @Streaming
    suspend fun getThumb(@Query("albumId") albumId: Long): Response<ResponseBody>
}