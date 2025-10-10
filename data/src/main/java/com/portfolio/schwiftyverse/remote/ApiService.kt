package com.portfolio.schwiftyverse.remote

import com.portfolio.schwiftyverse.dto.ApiResponseDto
import retrofit2.http.GET

interface ApiService {
    @GET("character")
    suspend fun getCharacters(): ApiResponseDto
}