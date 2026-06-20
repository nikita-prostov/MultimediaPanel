package com.nks.interactive.multimediapanel.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nks.interactive.multimediapanel.api.job.JobSseClient
import com.nks.interactive.multimediapanel.models.job.CurrentJobInfo
import kotlinx.coroutines.launch

class JobScreenVM(private val jobSseClient: JobSseClient) : ViewModel() {

    var jobInfo = mutableStateOf<CurrentJobInfo?>(null)

    fun connect(){
        viewModelScope.launch {
            jobSseClient.jobData.collect {
                jobInfo.value = it
            }
        }
    }

    fun disconnect(){
        jobSseClient.disconnect()
    }
}