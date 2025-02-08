package com.example.surfriders.modules.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postFirebaseModel = PostFirebaseModel()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        _isLoading.value = true
        postFirebaseModel.getAllPosts { postList ->
            _posts.value = postList
            _isLoading.value = false
            Log.d("FeedViewModel", "Fetched posts: ${postList.size}")
        }
    }

    fun refreshPosts() {
        fetchPosts()
    }
}

