package com.example.surfriders.data.location

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val GEOAPIFY_BASE_URL = "https://api.geoapify.com/v2/"
val apiKey = "61cddf3e65834b6cb4a3fb1963d0c47e"
val filter = "circle:34.0522,-118.2437,50000" // 50km radius around Los Angeles (lat,lon,radius)

class LocationService {
    companion object {
        val instance: LocationService = LocationService()
    }

    private val apiService: LocationService =
        RetrofitClient.retrofit.create(LocationService::class.java)

    object RetrofitClient {

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(GEOAPIFY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}