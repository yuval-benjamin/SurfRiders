package com.example.surfriders.modules.feed

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel
import com.example.surfriders.data.user.User
import com.example.surfriders.data.user.UserFirebaseModel

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val postFirebaseModel = PostFirebaseModel()
    private val userFirebaseModel = UserFirebaseModel()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchPostsWithUsers()
    }

    private fun fetchPostsWithUsers() {
        _isLoading.value = true
        postFirebaseModel.getAllPosts Error@{ postList ->
            for (post in postList) {
                postFirebaseModel.getImage(post.id) { postImage ->
                    post.postImage = postImage.toString()
                }
            }
            val updatedPosts = mutableListOf<Post>()
            var remainingRequests = postList.size

            if (postList.isEmpty()) {
                _posts.value = emptyList()
                _isLoading.value = false
                return@Error
            }

            postList.forEach { post ->
                userFirebaseModel.getUserById(post.userId) { user ->
                    userFirebaseModel.getImage(user.id) { uri: Uri ->
                        post.username = "${user.firstName} ${user.lastName}"
                        post.userProfileImage = uri.toString()
                        updatedPosts.add(post)

                        remainingRequests--
                        if (remainingRequests == 0) {
                            _posts.value = updatedPosts
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    fun refreshPosts() {
        fetchPostsWithUsers()
    }
}
