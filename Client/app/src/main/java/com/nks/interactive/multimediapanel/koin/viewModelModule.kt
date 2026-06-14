package com.nks.interactive.multimediapanel.koin

import com.nks.interactive.multimediapanel.viewModel.HomeScreenVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeScreenVM(get(), get(),get()) }
}