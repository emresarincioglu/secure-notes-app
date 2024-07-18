package com.example.securenotes.domain.home.di

import com.example.securenotes.data.home.repository.NoteRepository
import com.example.securenotes.domain.home.AddNoteUseCase
import com.example.securenotes.domain.home.DeleteNoteUseCase
import com.example.securenotes.domain.home.GetNoteContentUseCase
import com.example.securenotes.domain.home.GetNoteSummariesUseCase
import com.example.securenotes.domain.home.UpdateNoteOrderUseCase
import com.example.securenotes.domain.home.UpdateNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object HomeDomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetNoteSummariesUseCase(noteRepository: NoteRepository): GetNoteSummariesUseCase {
        return GetNoteSummariesUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSwapNotesUseCase(noteRepository: NoteRepository): UpdateNoteOrderUseCase {
        return UpdateNoteOrderUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideAddNoteUseCase(noteRepository: NoteRepository): AddNoteUseCase {
        return AddNoteUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateNoteUseCase(noteRepository: NoteRepository): UpdateNoteUseCase {
        return UpdateNoteUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteNoteUseCase(noteRepository: NoteRepository): DeleteNoteUseCase {
        return DeleteNoteUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNoteContentUseCase(noteRepository: NoteRepository): GetNoteContentUseCase {
        return GetNoteContentUseCase(noteRepository)
    }
}
