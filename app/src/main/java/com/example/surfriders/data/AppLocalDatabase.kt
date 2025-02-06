package com.example.surfriders.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.surfriders.SurfRidersApplication
import com.example.surfriders.data.location.Location
import com.example.surfriders.data.location.LocationDao
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostDao
import com.example.surfriders.data.user.User
import com.example.surfriders.data.user.UserDao

@Database(entities = [User::class, Location::class, Post::class], version = 8, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDto(): UserDao
    abstract fun locationDao(): LocationDao
    abstract fun postDao(): PostDao
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