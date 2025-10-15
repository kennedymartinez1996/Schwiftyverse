package com.portfolio.schwiftyverse.model

// This is the clean, non-nullable model that the rest of our app will use.
// It is completely decoupled from the API's data structure.
data class CharacterModel(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String,
    val gender: String,
    val origin: String,
    val lastKnownLocation: String,
    val type: String
)