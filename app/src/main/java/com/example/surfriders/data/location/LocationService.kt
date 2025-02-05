package com.example.surfriders.data.location

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val GEOAPIFY_BASE_URL = "https://api.geoapify.com/v2/"
const val apiKey = "61cddf3e65834b6cb4a3fb1963d0c47e"
const val category = "beach"
const val filter =
    "rect:33.91785157181864,32.75367877479448,35.45181884850199,31.525585493928073"  // for israel beach near Tel Aviv

interface GeoapifyService {
    @GET("places")
    suspend fun getBeaches(
        @Query("categories") categories: String = "beach",
        @Query("filter") filter: String,
        @Query("limit") limit: Int = 20,
        @Query("apiKey") apiKey: String
    ): GeoapifyResponse
}

interface UnsplashService {
    @GET("search/photos")
    suspend fun searchImages(
        @Query("query") query: String,  // The location name (e.g., beach name or city)
        @Query("client_id") apiKey: String  // Unsplash API key
    ): UnsplashResponse
}

// Unsplash API Key (replace with your actual API key)
const val UNSPLASH_API_KEY =
    "hGva-Wccvxl2R0Kk_-T0KyZFj_7tH5ea3gJOPXxxOLE"  // Replace this with your actual Unsplash API key

const val UNSPLASH_API_SECRET= "gbZNEMeufyF3HSWaGi3GgnOs2NOTJHdL447Erbh67TU"


class LocationService {
    companion object {
        val instance: LocationService = LocationService()
    }

    // Retrofit service instance
    private val apiService: GeoapifyService =
        RetrofitClient.retrofit.create(GeoapifyService::class.java)

    // Function to fetch beach locations
    suspend fun getLocations() {
        try {
            Log.d("BeachLocations", "Fetching beach data...")  // Log when starting

            val response = apiService.getBeaches(
                categories = category,
                filter = filter,
                limit = 20,
                apiKey = apiKey
            )

            Log.d(
                "BeachLocations",
                "Received ${response.features.size} features"
            )

            if (response.features.isEmpty()) {
                Log.d("BeachLocations", "No locations found.")
            }

            val locations = response.features.take(20)

            for (location in locations) {
                val props = location.properties
                val locationName = props.name ?: "Unknown Beach"
                val imageUrl = getImageForLocation(locationName)
                Log.d("BeachLocations", "Name: ${props.name ?: "Unknown"}")
                Log.d("BeachLocations", "City: ${props.city ?: "Unknown"}")
                Log.d("BeachLocations", "Image URL: $imageUrl")

            }
        } catch (e: Exception) {
            Log.e(
                "BeachLocations",
                "Error fetching location data: ${e.message}",
                e
            )
        }
    }

    private suspend fun getImageForLocation(locationName: String): String? {
        return try {
            val response =
                UnsplashClient.service.searchImages(query = locationName, apiKey = UNSPLASH_API_KEY)

            response.results.firstOrNull()?.urls?.regular
        } catch (e: Exception) {
            Log.e("BeachLocations", "Error fetching image for $locationName: ${e.message}", e)
            null
        }
    }

    object RetrofitClient {
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(GEOAPIFY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}

object UnsplashClient {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: UnsplashService = retrofit.create(UnsplashService::class.java)
}

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val urls: UnsplashPhotoUrls
)

data class UnsplashPhotoUrls(
    val regular: String
)

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: BeachProperties
)

data class BeachProperties(
    val name: String?,
    val country: String?,
    val city: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double
)
