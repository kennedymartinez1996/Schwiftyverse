package com.portfolio.schwiftyverse.usecase

import com.portfolio.schwiftyverse.repository.CharacterRepository

// This class represents a single business logic action.
// It has no Hilt annotations to keep the domain layer pure.
class GetCharactersUseCase(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(page: Int, name: String?) =
        repository.getCharacters(page = page, name = name)

}