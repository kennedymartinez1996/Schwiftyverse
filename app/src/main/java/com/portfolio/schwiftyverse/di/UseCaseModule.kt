package com.portfolio.schwiftyverse.di

import com.portfolio.schwiftyverse.repository.CharacterRepository
import com.portfolio.schwiftyverse.usecase.GetCharacterByIdUseCase
import com.portfolio.schwiftyverse.usecase.GetCharactersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetCharactersUseCase(repository: CharacterRepository): GetCharactersUseCase {
        return GetCharactersUseCase(repository)
    }

    @Provides
    fun provideGetCharacterByIdUseCase(repository: CharacterRepository): GetCharacterByIdUseCase {
        return GetCharacterByIdUseCase(repository)
    }
}