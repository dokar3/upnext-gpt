package com.dokar.upnextgpt.di

import io.upnextgpt.base.AppLauncher
import io.upnextgpt.base.ContextAppLauncher
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.data.settings.dataStore
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { androidContext().dataStore }
    single<AppLauncher> { ContextAppLauncher(context = androidContext()) }
    single { Settings(get()) }

    factory { NotificationBasedPlayer(context = androidContext()) }

    viewModel<HomeViewModel> {
        HomeViewModel(
            player = get(),
            settings = get(),
            appLauncher = get(),
        )
    }
}