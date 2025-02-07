package com.example.surfriders.data.location

import android.util.Log
import com.example.surfriders.data.AppLocalDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val GEOAPIFY_BASE_URL = "https://api.geoapify.com/v2/"
const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
const val geoApiKey = "61cddf3e65834b6cb4a3fb1963d0c47e"
const val unsplashApiKey =
    "hGva-Wccvxl2R0Kk_-T0KyZFj_7tH5ea3gJOPXxxOLE"
const val category = "beach"
const val filter =
    "rect:33.91785157181864,32.75367877479448,35.45181884850199,31.525585493928073"  // for israel beach near Tel Aviv

//interface GeoapifyService {
//    @GET("places")
//    suspend fun getBeaches(
//        @Query("categories") categories: String = "beach",
//        @Query("filter") filter: String,
//        @Query("limit") limit: Int = 20,
//        @Query("apiKey") apiKey: String
//    ): GeoapifyResponse
//}
//
//interface UnsplashService {
//    @GET("search/photos")
//    suspend fun searchImages(
//        @Query("query") query: String,  // The location name (e.g., beach name or city)
//        @Query("client_id") apiKey: String  // Unsplash API key
//    ): UnsplashResponse
//}

//const val UNSPLASH_API_SECRET= "gbZNEMeufyF3HSWaGi3GgnOs2NOTJHdL447Erbh67TU"


class LocationService {
    companion object {
        val instance: LocationService = LocationService()
    }

    private val apiService: GeoapifyService =
        RetrofitClient.retrofit.create(GeoapifyService::class.java)

    private val locationDao = AppLocalDatabase.db.locationDao()


    suspend fun getLocations(): List<Location> {
        val cachedLocations = locationDao.getAllLocations()
        if (cachedLocations.isNotEmpty()) {
            return cachedLocations
        }

        val locationsList = mutableListOf<Location>()
        try {
            Log.d("BeachLocations", "Fetching beach data...")  // Log when starting

            val response = apiService.getBeaches(
                categories = category,
                filter = filter,
                limit = 20,
                apiKey = geoApiKey
            )

            Log.d(
                "BeachLocations",
                "Received ${response.features.size} features"
            )

            if (response.features.isEmpty()) {
                Log.d("BeachLocations", "No locations found.")
                return emptyList()
            }

            val locations = response.features.take(20)

            for ((i, location) in locations.withIndex()) {
                val props = location.properties
                val locationName = props.name ?: "Unknown Beach"
                val imageUrl = getImageForLocation(locationName)
                Log.d("BeachLocations", "Name: ${props.name ?: "Unknown"}")
                Log.d("BeachLocations", "City: ${props.city ?: "Unknown"}")
                Log.d("BeachLocations", "Image URL: $imageUrl")
                val locationObj = Location(
                    locationId = i.toString(),
                    name = locationName,
                    city = props.city ?: "Unknown",
                    imageUrl = imageUrl
                )

                locationsList.add(locationObj)
            }
        } catch (e: Exception) {
            Log.e(
                "BeachLocations",
                "Error fetching location data: ${e.message}",
                e
            )
        }
        locationDao.insertLocations(locationsList)
        return locationsList
    }

    private suspend fun getImageForLocation(locationName: String): String? {
        return try {
            val response =
                UnsplashClient.service.searchImages(query = locationName, apiKey = unsplashApiKey)

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
        .baseUrl(UNSPLASH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: UnsplashService = retrofit.create(UnsplashService::class.java)
}
