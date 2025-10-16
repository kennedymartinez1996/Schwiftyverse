package com.portfolio.schwiftyverse.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.portfolio.schwiftyverse.di.DefaultUnknown
import com.portfolio.schwiftyverse.local.CharacterDao
import com.portfolio.schwiftyverse.local.toDomainModel
import com.portfolio.schwiftyverse.local.toEntity
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.remote.toDomainModel
import com.portfolio.schwiftyverse.worker.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val characterDao: CharacterDao,
    @ApplicationContext private val context: Context,
    @DefaultUnknown private val defaultUnknown: String,
) : CharacterRepository {

    override fun getCharactersStream(): Flow<List<CharacterModel>> {
        return characterDao.getCharactersStream().map { entities ->
            entities.map { it.toDomainModel(defaultUnknown) }
        }
    }

    override fun triggerSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    override suspend fun getCharacterById(id: Int): Result<CharacterModel> {
        val localCharacter = characterDao.getCharacterById(id)

        if (localCharacter != null) {
            return Result.success(localCharacter.toDomainModel(defaultUnknown))
        }

        return try {
            val dto = apiService.getCharacterById(id)
            val character = dto.toDomainModel(defaultUnknown)
            characterDao.upsertAll(listOf(character.toEntity()))
            Result.success(character)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}