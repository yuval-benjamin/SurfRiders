package com.example.surfriders.data.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocations(locations: List<Location>)

    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<Location>

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
}