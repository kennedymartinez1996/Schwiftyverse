package com.portfolio.schwiftyverse.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface SyncState {
    data object NotStarted : SyncState

    data class Syncing(val progress: Int) : SyncState
    data class Success(val lastSync: Date) : SyncState {
        fun formattedTime(): String {
            return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(lastSync)
        }
    }

    data object Failed : SyncState
}