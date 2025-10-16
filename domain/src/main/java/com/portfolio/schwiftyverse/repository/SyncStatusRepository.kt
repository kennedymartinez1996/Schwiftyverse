package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.SyncState
import kotlinx.coroutines.flow.StateFlow

interface SyncStatusRepository {
    fun getSyncState(): StateFlow<SyncState>
    suspend fun updateSyncState(state: SyncState)
}