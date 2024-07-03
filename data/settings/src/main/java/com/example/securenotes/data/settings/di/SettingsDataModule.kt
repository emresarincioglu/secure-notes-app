package com.example.securenotes.data.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.securenotes.data.authentication.local.SettingsDataSource
import com.example.securenotes.data.settings.DefaultSettingsRepository
import com.example.securenotes.data.settings.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataBindModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(repository: DefaultSettingsRepository): SettingsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsDataProvideModule {
    @Provides
    @Singleton
    @Named("settingsDataStore")
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(name = "settings")
        }
    }

    @Provides
    @Singleton
    @Named("authenticationDataStore")
    fun provideSettingsDataSource(@Named("settingsDataStore") dataStore: DataStore<Preferences>): SettingsDataSource {
        return SettingsDataSource(dataStore)
    }
}
