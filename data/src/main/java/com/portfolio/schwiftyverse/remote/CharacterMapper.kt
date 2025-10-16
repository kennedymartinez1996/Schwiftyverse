package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.CharacterDto
import com.portfolio.schwiftyverse.model.CharacterModel

fun CharacterDto.toDomainModel(defaultUnknown: String): CharacterModel {
    return CharacterModel(
        id = id ?: -1,
        name = name ?: defaultUnknown,
        status = status ?: defaultUnknown,
        species = species ?: defaultUnknown,
        imageUrl = image?: defaultUnknown,
        gender = gender ?: defaultUnknown,
        origin = origin?.name ?: defaultUnknown,
        lastKnownLocation = location?.name ?: defaultUnknown,
        type = type ?: defaultUnknown,
    )
}