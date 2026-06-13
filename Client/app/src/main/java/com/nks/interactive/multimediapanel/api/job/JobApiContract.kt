package com.nks.interactive.multimediapanel.api.job

import com.nks.interactive.multimediapanel.models.job.JobInfoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface JobApiContract {
    @GET("job/list")
    suspend fun getList(@Query("page") page:Int):List<JobInfoDto>
}