package com.nks.interactive.multimediapanel.koin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nks.interactive.multimediapanel.api.ServerClient
import com.nks.interactive.multimediapanel.gson.LocalDateTimeAdapter
import com.nks.interactive.multimediapanel.gson.LocalTimeAdapter
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import org.koin.dsl.module
import java.time.LocalDateTime
import java.time.LocalTime

val commonModule = module {
    single { AppDataStorage(get()) }
    single<ServerClient> {
        val appDataStorage = get<AppDataStorage>()
        ServerClient(appDataStorage.ipAddress, appDataStorage.port)
    }
}