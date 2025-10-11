package com.portfolio.schwiftyverse.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterDto(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "species") val species: String?,
    @field:Json(name = "image") val image: String?
)