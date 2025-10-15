package com.portfolio.schwiftyverse.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val imageUrl: String,
    val gender: String,
    val origin: String,
    val lastKnownLocation: String,
    val type: String
)