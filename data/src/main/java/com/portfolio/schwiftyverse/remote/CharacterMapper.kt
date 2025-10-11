package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.CharacterDto
import com.portfolio.schwiftyverse.model.CharacterModel

// Extension function to map a DTO to a domain model, handling potential nulls with default values.
fun CharacterDto.toDomainModel(defaultUnknown: String, defaultImageUrl: String): CharacterModel {
    return CharacterModel(
        id = id ?: -1, // Provide a default value (-1) if the API returns a null id
        name = name ?: defaultUnknown,
        status = status ?: defaultUnknown,
        species = species ?: defaultUnknown,
        imageUrl = image ?: defaultImageUrl
    )
}