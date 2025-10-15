package com.portfolio.schwiftyverse.repository

import com.portfolio.schwiftyverse.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharactersStream(): Flow<List<CharacterModel>>

    suspend fun getCharacterById(id: Int): Result<CharacterModel>

    fun triggerSync()
}