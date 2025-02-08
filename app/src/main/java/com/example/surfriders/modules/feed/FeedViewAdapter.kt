package com.example.surfriders.modules.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postFirebaseModel = PostFirebaseModel()

    // LiveData to hold the list of posts
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        val since = Post.lastUpdated
        postFirebaseModel.getAllPosts(since) { postList ->
            _posts.value = postList
        }
    }
}
