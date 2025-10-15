package com.portfolio.schwiftyverse.local

import com.portfolio.schwiftyverse.model.CharacterModel


fun CharacterEntity.toDomainModel(
    defaultUnknown: String,
    defaultImageUrl: String
): CharacterModel {
    return CharacterModel(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = imageUrl.ifEmpty { defaultImageUrl },
        gender = gender,
        origin = origin,
        lastKnownLocation = lastKnownLocation.ifEmpty { defaultUnknown },
        type = type.ifEmpty { defaultUnknown }
    )
}


fun CharacterModel.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = imageUrl,
        gender = gender,
        origin = origin,
        lastKnownLocation = lastKnownLocation,
        type = type
    )
}