package com.portfolio.schwiftyverse.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseDto(
    @field:Json(name = "results") val results: List<CharacterDto>
)