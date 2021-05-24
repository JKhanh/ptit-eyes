package com.ptit.theeyes.di

import com.ptit.theeyes.utils.AppDispatchers
import com.ptit.theeyes.viewModel.CameraViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { AppDispatchers(Dispatchers.Main, Dispatchers.IO, Dispatchers.Default) }

    viewModel { CameraViewModel() }
}