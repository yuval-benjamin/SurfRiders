package com.example.surfriders.data.location

import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyService {
    @GET("places")
    suspend fun getBeaches(
        @Query("categories") categories: String = "beach",
        @Query("filter") filter: String,
        @Query("limit") limit: Int = 20,
        @Query("apiKey") apiKey: String
    ): GeoapifyResponse
}

data class GeoapifyResponse(val features: List<Feature>)
data class Feature(val properties: BeachProperties)

data class BeachProperties(
    val name: String?,
    val country: String?,
    val city: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val placeId: String
)
