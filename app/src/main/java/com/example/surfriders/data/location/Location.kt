package com.example.surfriders.data.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val city: String?,
    val imageUrl: String?,
    val locationId: String
)
