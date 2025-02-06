package com.example.surfriders.data.location

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {
    @GET("search/photos")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("client_id") apiKey: String
    ): UnsplashResponse
}

data class UnsplashResponse(val results: List<UnsplashPhoto>)
data class UnsplashPhoto(val urls: UnsplashPhotoUrls)
data class UnsplashPhotoUrls(val regular: String)
