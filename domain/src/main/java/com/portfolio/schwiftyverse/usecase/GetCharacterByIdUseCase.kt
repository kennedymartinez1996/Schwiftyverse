package com.portfolio.schwiftyverse.usecase

import com.portfolio.schwiftyverse.repository.CharacterRepository

class GetCharacterByIdUseCase(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int) = repository.getCharacterById(id)
}