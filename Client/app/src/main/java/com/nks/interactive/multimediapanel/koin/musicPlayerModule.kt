package com.nks.interactive.multimediapanel.koin

import com.nks.interactive.multimediapanel.api.music.MusicApiClient
import com.nks.interactive.multimediapanel.api.music.MusicApiContract
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import org.koin.dsl.module

val musicPlayerModule = module {
    single<MusicApiClient> {
        MusicApiClient("192.168.137.1", "5110")
    }

    single<MusicApiContract> { get<MusicApiClient>().api }
    single<MusicSseClient> { get<MusicApiClient>().sse }
}