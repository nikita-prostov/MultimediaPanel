package com.nks.interactive.multimediapanel.ui.screens.job

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nks.interactive.multimediapanel.R
import com.nks.interactive.multimediapanel.ui.components.HorizontalMenu
import com.nks.interactive.multimediapanel.ui.components.MenuItem
import com.nks.interactive.multimediapanel.viewModel.JobScreenVM
import org.koin.androidx.compose.koinViewModel

@Composable
fun JobScreen(){
    val vm = koinViewModel<JobScreenVM>()

    LaunchedEffect(Unit) {
        vm.connect()
    }
    DisposableEffect(Unit) {
        onDispose {
            vm.disconnect()
        }
    }

    Column {
        CurrentJobScreen(Modifier.weight(1f).fillMaxWidth().padding(start = 24.dp),vm)
    }
}