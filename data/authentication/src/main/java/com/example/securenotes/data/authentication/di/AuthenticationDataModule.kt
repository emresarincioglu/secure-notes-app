package com.example.securenotes.data.authentication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.securenotes.data.authentication.local.AuthenticationDataSource
import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.authentication.repository.DefaultAuthenticationRepository
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
abstract class AuthenticationDataBindModule {
    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        repository: DefaultAuthenticationRepository
    ): AuthenticationRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationDataProvideModule {
    @Provides
    @Singleton
    @Named("authenticationDataStore")
    fun provideAuthenticationDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(name = "authentication")
        }
    }

    @Provides
    @Singleton
    fun provideAuthenticationDataSource(
        @Named("authenticationDataStore") dataStore: DataStore<Preferences>
    ): AuthenticationDataSource {
        return AuthenticationDataSource(dataStore)
    }
}
