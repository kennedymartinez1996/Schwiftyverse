package com.portfolio.schwiftyverse.usecase

import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
    
class GetCharactersUseCase(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<List<CharacterModel>> = repository.getCharactersStream()

}