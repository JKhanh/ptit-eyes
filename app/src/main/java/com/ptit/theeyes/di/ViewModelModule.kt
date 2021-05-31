package com.ptit.theeyes.di

import com.ptit.theeyes.utils.AppDispatchers
import com.ptit.theeyes.viewModel.CameraViewModel
import com.ptit.theeyes.viewModel.DetectViewModel
import com.ptit.theeyes.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { AppDispatchers(Dispatchers.Main, Dispatchers.IO, Dispatchers.Default) }
    viewModel { MainViewModel() }
    viewModel { CameraViewModel() }
    viewModel { DetectViewModel(get(), androidContext()) }
}