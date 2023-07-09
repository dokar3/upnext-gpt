package com.dokar.upnextgpt.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.upnextgpt.Database
import io.upnextgpt.base.AppLauncher
import io.upnextgpt.base.ContextAppLauncher
import io.upnextgpt.data.api.Api
import io.upnextgpt.data.api.ApiImpl
import io.upnextgpt.data.dao.TrackDao
import io.upnextgpt.data.fetcher.GptNextTrackFetcher
import io.upnextgpt.data.fetcher.NextTrackFetcher
import io.upnextgpt.data.repository.TrackRepository
import io.upnextgpt.data.settings.Settings
import io.upnextgpt.data.settings.SettingsImpl
import io.upnextgpt.data.settings.dataStore
import io.upnextgpt.remote.palyer.NotificationBasedPlayer
import io.upnextgpt.ui.home.viewmodel.HomeViewModel
import io.upnextgpt.ui.settings.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single<AppLauncher> { ContextAppLauncher(context = androidContext()) }
    single<Settings> { SettingsImpl(get()) }
    single<Api> { ApiImpl(settings = get()) }
    single<NextTrackFetcher> { GptNextTrackFetcher(api = get()) }
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = androidContext(),
            name = "app.db",
        )
    }
    single<Database> { Database(driver = get()) }
    single { NotificationBasedPlayer(context = androidContext()) }

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

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            player = get(),
            api = get(),
            settings = get(),
        )
    }
}