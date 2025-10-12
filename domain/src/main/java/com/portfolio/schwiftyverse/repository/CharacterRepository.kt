package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.CharacterModel

// This interface in the domain layer defines a contract for data operations.
interface CharacterRepository {
    // Defines a function to get a list of characters, returning a Result
    // to handle success or failure gracefully.
    suspend fun getCharacters(): Result<List<CharacterModel>>

    suspend fun getCharacterById(id: Int): Result<CharacterModel>
}