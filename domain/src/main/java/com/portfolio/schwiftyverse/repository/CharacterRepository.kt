package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.PaginatedData

// This interface in the domain layer defines a contract for data operations.
interface CharacterRepository {

    suspend fun getCharacters(
        page: Int,
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): Result<PaginatedData<CharacterModel>>

    suspend fun getCharacterById(id: Int): Result<CharacterModel>
}