package com.nks.interactive.multimediapanel.koin

import com.nks.interactive.multimediapanel.api.ServerClient
import com.nks.interactive.multimediapanel.api.commonData.CommonSseClient
import com.nks.interactive.multimediapanel.api.health.HealthCheckApiContract
import com.nks.interactive.multimediapanel.api.job.JobSseClient
import com.nks.interactive.multimediapanel.api.music.MusicApiContract
import com.nks.interactive.multimediapanel.api.music.MusicSseClient
import com.nks.interactive.multimediapanel.api.transport.TransportInfoApiContract
import com.nks.interactive.multimediapanel.api.transport.TransportInfoSseClient
import org.koin.dsl.module

val apiModule = module {
    single<MusicApiContract> { get<ServerClient>().musicApi }
    single<HealthCheckApiContract> { get<ServerClient>().healthApi }
    single<JobApiContract> { get<ServerClient>().jobApi }
    single<TransportInfoApiContract> { get<ServerClient>().transportInfoApi }

    single<MusicSseClient> { get<ServerClient>().musicSse }
    single<CommonSseClient> { get<ServerClient>().commonSse }
    single<JobSseClient> { get<ServerClient>().jobSse }
    single<TransportInfoSseClient> { get<ServerClient>().transportInfoSse }
}