package com.example.surfriders.data.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Query("SELECT * FROM post")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM post WHERE id = :postId LIMIT 1")
    fun getPostById(postId: String): Post?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPost(post: Post)
}
