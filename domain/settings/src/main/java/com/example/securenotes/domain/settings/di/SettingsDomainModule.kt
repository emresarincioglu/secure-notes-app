package com.example.securenotes.domain.settings.di

import com.example.securenotes.data.authentication.repository.AuthenticationRepository
import com.example.securenotes.data.settings.SettingsRepository
import com.example.securenotes.domain.settings.GetSettingsUseCase
import com.example.securenotes.domain.settings.IsPasswordCorrectUseCase
import com.example.securenotes.domain.settings.LogOutUseCase
import com.example.securenotes.domain.settings.SetAuthenticationAttemptLimitUseCase
import com.example.securenotes.domain.settings.SetBiometricAuthenticationUseCase
import com.example.securenotes.domain.settings.SetPasswordUseCase
import com.example.securenotes.domain.settings.SetScreenLockAuthenticationUseCase
import com.example.securenotes.domain.settings.SetSessionTimeoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SettingsDomainModule {

    @ViewModelScoped
    @Provides
    fun provideSetPasswordUseCase(
        authenticationRepository: AuthenticationRepository,
        settingsRepository: SettingsRepository
    ): SetPasswordUseCase {
        return SetPasswordUseCase(authenticationRepository, settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideLogOutUseCase(authenticationRepository: AuthenticationRepository): LogOutUseCase {
        return LogOutUseCase(authenticationRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideIsPasswordCorrectUseCase(
        authenticationRepository: AuthenticationRepository
    ): IsPasswordCorrectUseCase {
        return IsPasswordCorrectUseCase(authenticationRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideGetSettingsUseCase(settingsRepository: SettingsRepository): GetSettingsUseCase {
        return GetSettingsUseCase(settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideSetBiometricAuthenticationUseCase(
        settingsRepository: SettingsRepository
    ): SetBiometricAuthenticationUseCase {
        return SetBiometricAuthenticationUseCase(settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideSetScreenLockAuthenticationUseCase(
        settingsRepository: SettingsRepository
    ): SetScreenLockAuthenticationUseCase {
        return SetScreenLockAuthenticationUseCase(settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideSetSessionTimeoutUseCase(
        authenticationRepository: AuthenticationRepository,
        settingsRepository: SettingsRepository
    ): SetSessionTimeoutUseCase {
        return SetSessionTimeoutUseCase(authenticationRepository, settingsRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideSetAuthenticationAttemptLimitUseCase(
        settingsRepository: SettingsRepository
    ): SetAuthenticationAttemptLimitUseCase {
        return SetAuthenticationAttemptLimitUseCase(settingsRepository)
    }
}
