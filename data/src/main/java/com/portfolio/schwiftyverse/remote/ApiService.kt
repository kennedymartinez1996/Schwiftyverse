package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.CharacterDto
import com.portfolio.schwiftyverse.dto.PaginatedResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): PaginatedResponseDto<CharacterDto>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}