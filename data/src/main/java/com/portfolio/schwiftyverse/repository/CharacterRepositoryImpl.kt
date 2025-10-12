package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.di.DefaultImage
import com.portfolio.schwiftyverse.di.DefaultUnknown
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.remote.toDomainModel
import javax.inject.Inject

// This class is clean of Android Context, injecting the required strings directly via Hilt.
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @DefaultUnknown private val defaultUnknown: String,
    @DefaultImage private val defaultImage: String
) : CharacterRepository {

    override suspend fun getCharacters(page: Int): Result<List<CharacterModel>> {
        return try {
            val response = apiService.getCharacters(page = page)
            val characters = response.results.map {
                it.toDomainModel(
                    defaultUnknown = defaultUnknown,
                    defaultImageUrl = defaultImage
                )
            }
            Result.success(characters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharacterById(id: Int): Result<CharacterModel> {
        return try {
            val dto = apiService.getCharacterById(id)
            val character = dto.toDomainModel(defaultUnknown, defaultImage)
            Result.success(character)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}