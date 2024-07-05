package com.example.securenotes.domain.authentication.di

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import com.example.securenotes.domain.authentication.GetAuthenticationDataUseCase
import com.example.securenotes.domain.authentication.GetIsAuthenticatedStreamUseCase
import com.example.securenotes.domain.authentication.IsPasswordCreatedUseCase
import com.example.securenotes.domain.authentication.LogInWithPasswordUseCase
import com.example.securenotes.domain.authentication.SetFailedAuthenticationAttemptsUseCase
import com.example.securenotes.domain.authentication.StartAuthenticationSessionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AuthenticationDomainModule {
    @ViewModelScoped
    @Provides
    fun provideSetFailedAuthenticationAttemptsUseCase(
        authenticationRepository: AuthenticationRepository
    ): SetFailedAuthenticationAttemptsUseCase {
        return SetFailedAuthenticationAttemptsUseCase(authenticationRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideGetIsAuthenticatedStreamUseCase(
        authenticationRepository: AuthenticationRepository
    ): GetIsAuthenticatedStreamUseCase {
        return GetIsAuthenticatedStreamUseCase(authenticationRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideLogInWithPasswordUseCase(
        authenticationRepository: AuthenticationRepository,
        startAuthenticationSessionUseCase: StartAuthenticationSessionUseCase
    ): LogInWithPasswordUseCase {
        return LogInWithPasswordUseCase(authenticationRepository, startAuthenticationSessionUseCase)
    }

    @ViewModelScoped
    @Provides
    fun provideStartAuthenticationSessionUseCase(
        authenticationRepository: AuthenticationRepository,
        settingsRepository: SettingsRepository
    ): StartAuthenticationSessionUseCase {
        return StartAuthenticationSessionUseCase(authenticationRepository, settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideIsPasswordCreatedUseCase(
        authenticationRepository: AuthenticationRepository
    ): IsPasswordCreatedUseCase {
        return IsPasswordCreatedUseCase(authenticationRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideGetAuthenticationDataUseCase(
        authenticationRepository: AuthenticationRepository,
        settingsRepository: SettingsRepository
    ): GetAuthenticationDataUseCase {
        return GetAuthenticationDataUseCase(authenticationRepository, settingsRepository)
    }
}
