package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.ApiResponseDto
import com.portfolio.schwiftyverse.dto.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("character")
    suspend fun getCharacters(): ApiResponseDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}