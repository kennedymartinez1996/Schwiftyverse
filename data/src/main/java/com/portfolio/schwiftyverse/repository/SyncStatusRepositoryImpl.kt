package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.SyncState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStatusRepositoryImpl @Inject constructor() : SyncStatusRepository {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.NotStarted)

    override fun getSyncState(): StateFlow<SyncState> = _syncState.asStateFlow()

    override suspend fun updateSyncState(state: SyncState) {
        _syncState.value = state
    }
}