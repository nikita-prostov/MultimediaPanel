package com.nks.interactive.multimediapanel.api.music

import com.google.gson.Gson
import com.nks.interactive.multimediapanel.models.PlayerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class MusicSseClient(private val apiUrl: String) {
    private val client = OkHttpClient()
    private var eventSource: EventSource? = null

    val playerState: Flow<PlayerState> = callbackFlow {
        val url = "$apiUrl/music/connect"
        val request = Request.Builder().url(url).build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val state = Gson().fromJson(data, PlayerState::class.java)
                    trySend(state)
                } catch (e: Exception) {
                    // Игнорируем ошибки парсинга
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                // Переподключение через 3 секунды
                // Можно emit ошибку или закрыть flow
            }
        }

        eventSource = EventSources.createFactory(client).newEventSource(request, listener)

        awaitClose {
            eventSource?.cancel()
        }
    }

    fun disconnect() {
        eventSource?.cancel()
    }
}