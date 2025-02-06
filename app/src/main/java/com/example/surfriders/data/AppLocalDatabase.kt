package com.example.surfriders.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.surfriders.SurfRidersApplication
import com.example.surfriders.data.location.Location
import com.example.surfriders.data.location.LocationDao
import com.example.surfriders.data.user.User
import com.example.surfriders.data.user.UserDao

@Database(entities = [User::class, Location::class], version = 7, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDto(): UserDao
    abstract fun locationDao(): LocationDao
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = SurfRidersApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "SurfRiders"
        ).fallbackToDestructiveMigration()
            .build()
    }
}