package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.di.DefaultImage
import com.portfolio.schwiftyverse.di.DefaultUnknown
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.InfoModel
import com.portfolio.schwiftyverse.model.PaginatedData
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.remote.toDomainModel
import javax.inject.Inject

// This class is clean of Android Context, injecting the required strings directly via Hilt.
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @DefaultUnknown private val defaultUnknown: String,
    @DefaultImage private val defaultImage: String
) : CharacterRepository {

    override suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): Result<PaginatedData<CharacterModel>> {
        return try {
            val response = apiService.getCharacters(page, name, status, species, type, gender)
            val characters = response.results.map {
                it.toDomainModel(defaultUnknown, defaultImage)
            }
            val info = InfoModel(
                count = response.info.count ?: 0,
                pages = response.info.pages ?: 0,
                hasNext = response.info.next != null,
                hasPrev = response.info.prev != null
            )
            val paginatedData = PaginatedData(
                info = info,
                data = characters
            )
            Result.success(paginatedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharacterById(id: Int): Result<CharacterModel> {
        return try {
            val response = apiService.getCharacterById(id = id)
            val character = response.toDomainModel(defaultUnknown, defaultImage)
            Result.success(character)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}