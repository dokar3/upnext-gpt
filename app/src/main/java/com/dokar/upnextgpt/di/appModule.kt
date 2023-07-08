package com.dokar.upnextgpt.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.upnextgpt.base.AppLauncher
import io.upnextgpt.base.ContextAppLauncher
import io.upnextgpt.data.api.NextTrackService
import io.upnextgpt.data.fetcher.GptNextTrackFetcher
import io.upnextgpt.data.fetcher.NextTrackFetcher
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.data.settings.dataStore
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

private const val API_BASE_URL = "https://upnextgpt.vercel.app"

val appModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single<AppLauncher> { ContextAppLauncher(context = androidContext()) }
    single<Settings> { Settings(get()) }

    single<NextTrackService> {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create<NextTrackService>()
    }

    single<NextTrackFetcher> { GptNextTrackFetcher(nextTrackService = get()) }

    factory { NotificationBasedPlayer(context = androidContext()) }

    viewModel<HomeViewModel> {
        HomeViewModel(
            player = get(),
            settings = get(),
            appLauncher = get(),
            nextTrackFetcher = get(),
        )
    }
}