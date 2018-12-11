package com.mybrowser.mybrowser.di

import com.mybrowser.mybrowser.ui.main.MainViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

private val viewModelModule = applicationContext {
    viewModel { MainViewModel() }
}

val appModules = arrayListOf(viewModelModule)