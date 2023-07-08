package com.dokar.upnextgpt.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.upnextgpt.Database
import io.upnextgpt.base.AppLauncher
import io.upnextgpt.base.ContextAppLauncher
import io.upnextgpt.data.api.NextTrackService
import io.upnextgpt.data.dao.TrackDao
import io.upnextgpt.data.fetcher.GptNextTrackFetcher
import io.upnextgpt.data.fetcher.NextTrackFetcher
import io.upnextgpt.data.repository.TrackRepository
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

    single<SqlDriver> {
        AndroidSqliteDriver(
            Database.Schema,
            androidContext(),
            "app.db"
        )
    }

    single<Database> { Database(driver = get()) }

    factory { NotificationBasedPlayer(context = androidContext()) }

    factory { TrackDao(database = get()) }

    factory { TrackRepository(trackDao = get()) }

    viewModel<HomeViewModel> {
        HomeViewModel(
            player = get(),
            settings = get(),
            appLauncher = get(),
            nextTrackFetcher = get(),
            trackRepo = get(),
        )
    }
}