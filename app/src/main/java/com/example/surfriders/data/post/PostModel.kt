package com.example.surfriders.data.post

import android.net.Uri
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

    fun refreshAllPosts() {
        firebaseModel.getUserPosts { list ->
            var time = Post.lastUpdated  // Keep track of the latest timestamp
            for (post in list) {
                post.timestamp?.let {
                    if (time < it) time = it
                }
                postsExecutor.execute {
                    database.postDao().insertPost(post)
                }
                Post.lastUpdated = time  // Update the lastUpdated value
            }
        }
    }


    fun addPost(post: Post, selectedImageUri: Uri, callback: () -> Unit) {

        firebaseModel.addPost(post) {
            firebaseModel.addPostImage(post.id, selectedImageUri) {
                refreshAllPosts()
                callback()
            }
        }
    }
}