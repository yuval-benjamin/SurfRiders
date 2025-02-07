package com.example.surfriders.data.post

import androidx.lifecycle.LiveData
import com.example.surfriders.data.AppLocalDatabase
import java.util.concurrent.Executors

class PostModel private constructor() {
    private val database = AppLocalDatabase.db
    private var postsExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = PostFirebaseModel()

    companion object {
        val instance: PostModel = PostModel()
    }

    fun getAllPosts(): LiveData<List<Post>> {
        refreshAllPosts()
        return database.postDao().getAllPosts()
    }

    private fun refreshAllPosts() {
        val lastUpdated: Long = Post.lastUpdated

        firebaseModel.getAllPosts(lastUpdated) { list ->
            var time = lastUpdated
            for (post in list) {
                post.timestamp?.let {
                    if (time < it) time = it
                }
                postsExecutor.execute {
                    database.postDao().insertPost(post)
                }
                Post.lastUpdated = time
            }
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        firebaseModel.addPost(post) {
            refreshAllPosts()
            callback()
        }
    }
}