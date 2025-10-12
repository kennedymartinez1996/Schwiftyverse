package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.PaginatedData

// This interface in the domain layer defines a contract for data operations.
interface CharacterRepository {
    // Defines a function to get a list of characters, returning a Result
    // to handle success or failure gracefully.
    suspend fun getCharacters(page: Int, name: String?): Result<PaginatedData<CharacterModel>>

    suspend fun getCharacterById(id: Int): Result<CharacterModel>
}