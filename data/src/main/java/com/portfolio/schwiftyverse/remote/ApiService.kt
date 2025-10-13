package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.ApiResponseDto
import com.portfolio.schwiftyverse.dto.CharacterDto
import com.portfolio.schwiftyverse.dto.PaginatedResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("species") species: String?,
        @Query("type") type: String?,
        @Query("gender") gender: String?
    ): PaginatedResponseDto<CharacterDto>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}