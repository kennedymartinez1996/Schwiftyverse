package com.portfolio.schwiftyverse.di

import com.portfolio.schwiftyverse.repository.CharacterRepository
import com.portfolio.schwiftyverse.repository.CharacterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Binds the CharacterRepository interface to its implementation.
    // @Binds is more efficient than @Provides for this simple case.
    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository
}