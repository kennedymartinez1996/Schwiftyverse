package com.portfolio.schwiftyverse.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.portfolio.schwiftyverse.di.DefaultUnknown
import com.portfolio.schwiftyverse.local.AppDatabase
import com.portfolio.schwiftyverse.local.CharacterDao
import com.portfolio.schwiftyverse.local.toEntity
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.SyncState
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.remote.toDomainModel
import com.portfolio.schwiftyverse.repository.SyncStatusRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Date

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val characterDao: CharacterDao,
    private val appDatabase: AppDatabase,
    private val imageLoader: ImageLoader,
    private val syncStatusRepository: SyncStatusRepository,
    @DefaultUnknown private val defaultUnknown: String
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val remoteCharacters = mutableListOf<CharacterModel>()
            var page = 1
            var hasNext = true
            var totalPages = 1
            while (hasNext) {
                val response = apiService.getCharacters(page = page, name = null)
                totalPages = response.info?.pages ?: totalPages
                val characters =
                    response.results?.map { it.toDomainModel(defaultUnknown) }
                        ?: emptyList()
                if (characters.isNotEmpty()) {
                    remoteCharacters.addAll(characters)
                }

                val progress = ((page.toFloat() / totalPages.toFloat()) * 100).toInt()
                syncStatusRepository.updateSyncState(SyncState.Syncing(progress))

                hasNext = response.info?.next != null
                if (hasNext) page++
            }

            val remoteIds = remoteCharacters.map { it.id }.toSet()

            val localCharacters = characterDao.getCharactersStream().first()

            val charactersToDelete = localCharacters.filter { it.id !in remoteIds }

            appDatabase.withTransaction {
                if (charactersToDelete.isNotEmpty()) {
                    characterDao.deleteCharacters(charactersToDelete)
                }
                characterDao.upsertAll(remoteCharacters.map { it.toEntity() })
            }

            remoteCharacters.forEach { character ->
                if (character.imageUrl.isNotEmpty()) {
                    val request = ImageRequest.Builder(applicationContext)
                        .data(character.imageUrl)
                        .build()
                    imageLoader.enqueue(request)
                }
            }
            syncStatusRepository.updateSyncState(SyncState.Success(Date()))
            return Result.success()

        } catch (e: Exception) {
            syncStatusRepository.updateSyncState(SyncState.Failed)
            return Result.failure()
        }
    }
}