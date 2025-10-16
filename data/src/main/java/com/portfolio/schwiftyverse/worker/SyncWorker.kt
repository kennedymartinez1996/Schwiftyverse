package com.portfolio.schwiftyverse.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.portfolio.schwiftyverse.di.DefaultImage
import com.portfolio.schwiftyverse.di.DefaultUnknown
import com.portfolio.schwiftyverse.local.AppDatabase
import com.portfolio.schwiftyverse.local.CharacterDao
import com.portfolio.schwiftyverse.local.toEntity
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.remote.toDomainModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val characterDao: CharacterDao,
    private val appDatabase: AppDatabase,
    private val imageLoader: ImageLoader,
    @DefaultUnknown private val defaultUnknown: String,
    @DefaultImage private val defaultImage: String
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val remoteCharacters = mutableListOf<CharacterModel>()
            var page = 1
            var hasNext = true
            while (hasNext) {
                val response = apiService.getCharacters(page = page, name = null)
                val characters =
                    response.results?.map { it.toDomainModel(defaultUnknown, defaultImage) }
                        ?: emptyList()
                if (characters.isNotEmpty()) {
                    remoteCharacters.addAll(characters)
                }

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

            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }
}