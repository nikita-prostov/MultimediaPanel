package com.nks.interactive.multimediapanel.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.job.JobApiContract
import com.nks.interactive.multimediapanel.api.job.JobSseClient
import com.nks.interactive.multimediapanel.models.job.CurrentJobInfo
import com.nks.interactive.multimediapanel.models.job.JobInfoDto
import kotlinx.coroutines.launch

class JobScreenVM(private val jobSseClient: JobSseClient, private val jobApi: JobApiContract) : ViewModel() {

    var jobInfo = mutableStateOf<CurrentJobInfo?>(null)
    var isLoading = mutableStateOf(false)
    var history = mutableStateOf<List<JobInfoDto>>(emptyList())

    fun connect(){
        viewModelScope.launch {
            jobSseClient.jobData.collect {
                jobInfo.value = it
            }
        }
    }

    fun getHistory(page: Int = 1){
        isLoading.value = true
        if(page == 1){
            history.value = emptyList()
        }
        viewModelScope.launch {
            val old = history.value.toMutableList()
            val res = jobApi.getList(page)
            old.addAll(res)
            history.value = old
        }
    }

    fun disconnect(){
        jobSseClient.disconnect()
    }
}