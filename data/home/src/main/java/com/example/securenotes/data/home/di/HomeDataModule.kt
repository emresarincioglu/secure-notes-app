package com.example.securenotes.data.home.di

import android.content.Context
import androidx.room.Room
import com.example.securenotes.data.home.local.NoteDataSource
import com.example.securenotes.data.home.local.database.NoteDatabase
import com.example.securenotes.data.home.repository.DefaultNoteRepository
import com.example.securenotes.data.home.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataBindModule {
    @Binds
    @Singleton
    abstract fun bindNoteRepository(repository: DefaultNoteRepository): NoteRepository
}

@Module
@InstallIn(SingletonComponent::class)
object HomeDataProvideModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(context, NoteDatabase::class.java, "notes").build()
    }

    @Provides
    @Singleton
    fun provideNoteDataSource(
        @ApplicationContext context: Context,
        database: NoteDatabase
    ): NoteDataSource {
        return NoteDataSource(context, database)
    }
}
