package com.example.surfriders.data.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDTO {
    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<MutableList<User>>

    @Query("SELECT * FROM user where id = :userId")
    fun getUserById(userId: String): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)
}