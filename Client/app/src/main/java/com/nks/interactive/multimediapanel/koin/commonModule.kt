package com.nks.interactive.multimediapanel.koin

import com.nks.interactive.multimediapanel.api.ServerClient
import com.nks.interactive.multimediapanel.localStorage.AppDataStorage
import org.koin.dsl.module

val commonModule = module {
    single { AppDataStorage(get()) }
    single<ServerClient> {
        val appDataStorage = get<AppDataStorage>()
        ServerClient(appDataStorage.ipAddress, appDataStorage.port)
    }
}