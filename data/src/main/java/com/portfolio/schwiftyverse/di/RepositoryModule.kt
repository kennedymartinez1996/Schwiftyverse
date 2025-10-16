package com.portfolio.schwiftyverse.di

import com.portfolio.schwiftyverse.repository.CharacterRepository
import com.portfolio.schwiftyverse.repository.CharacterRepositoryImpl
import com.portfolio.schwiftyverse.repository.SyncStatusRepository
import com.portfolio.schwiftyverse.repository.SyncStatusRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindSyncStatusRepository(
        syncStatusRepositoryImpl: SyncStatusRepositoryImpl
    ): SyncStatusRepository
}