package com.example.surfriders.modules.search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel
import com.example.surfriders.data.user.UserFirebaseModel

class PostDetailViewModel : ViewModel() {
    val post = MutableLiveData<Post?>()
    private val postModel = PostFirebaseModel()
    private val userModel = UserFirebaseModel()

    fun loadPost(postId: String?) {
        if (postId == null) return

        postModel.getPost(postId) { fetchedPost ->

            if (fetchedPost != null) {
                userModel.getUserById(fetchedPost.userId) { user ->
                    userModel.getImage(user.id) { uri: Uri ->
                        fetchedPost.username = "${user.firstName} ${user.lastName}"
                        fetchedPost.userProfileImage = uri.toString()

                        postModel.getImage(fetchedPost.id) { postImageUri ->
                            fetchedPost.postImage = postImageUri.toString()

                            post.postValue(fetchedPost)
                        }
                    }
                }
            } else {
                Log.d("PostDetailViewModel", "No post found for ID: $postId")
            }
        }
    }
}
