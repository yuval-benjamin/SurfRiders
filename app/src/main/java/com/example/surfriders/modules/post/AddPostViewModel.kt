package com.example.surfriders.modules.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.UUID

class AddPostViewModel : ViewModel() {

    var imageURI: MutableLiveData<Uri> = MutableLiveData()
    var locationName: String = ""
    var postText: String = ""
    var grade: Int? = 0
    var existingImageUrl: String? = null

    private val auth = Firebase.auth

    fun createPost(
        locationId: String,
        createdPostCallback: () -> Unit
    ) {
        if (validatePost()) {
            val postId = UUID.randomUUID().toString()
            val userId = auth.currentUser!!.uid

            val post = Post(
                id = postId,
                text = postText,
                grade = grade!!,
                userId = userId,
                locationId = locationId,
                locationName = locationName
            )

            PostModel.instance.addPost(post, imageURI.value!!) {
                createdPostCallback()
            }
        }
    }

    fun savePost(
        postId: String? = null,
        locationId: String,
        createdPostCallback: () -> Unit,
        updatePostCallback: () -> Unit
    ) {
        if (validatePost()) {
            val post = Post(
                id = postId ?: UUID.randomUUID().toString(),
                text = postText,
                grade = grade!!,
                userId = auth.currentUser!!.uid,
                locationId = locationId,
                locationName = locationName
            )
            if (postId == null) {
                PostModel.instance.addPost(post, imageURI.value!!) {
                    createdPostCallback()
                }
            } else {
                PostModel.instance.updatePost(post) {
                    updatePostCallback()
                }
                // Upload a new image only if a new one is selected
                if (imageURI.value != null && !imageURI.value.toString().startsWith("https://")) {
                    PostModel.instance.updatePostImage(post.id, imageURI.value!!) {
                        updatePostCallback()
                    }
//                    PostModel.instance.updatePostImage(post.id, imageURI.value!!) {
//                        updatePostCallback()
//                    }
                }
            }
        }
    }


    fun updatePost(
        postId: String,
        postText: String,
        grade: Int?,
        imageUri: Uri?,
        updatePostCallback: () -> Unit
    ) {
        if (validatePost()) {
            val userId = auth.currentUser!!.uid

            val post = Post(
                id = postId,
                text = postText,
                grade = grade!!,
                userId = userId,
                locationId = "",
                locationName = locationName,
            )

            PostModel.instance.updatePost(post) {
                updatePostCallback()
            }
            PostModel.instance.updatePostImage(post.id, imageURI.value!!) {
                updatePostCallback()
            }
        }
    }

    // Get a post by its ID
    fun getPost(postId: String, postCallback: (Post) -> Unit) {
        PostModel.instance.getPost(postId) { post ->
            if (post != null) {
                postCallback(post)
            }
        }
    }

    private fun validatePost(): Boolean {
        var valid = true

        if (postText.isEmpty()) {
            valid = false
        }
        if (grade == null) {
            valid = false
        } else if (grade!! < 1 || grade!! > 5) {
            valid = false
        }
        if (imageURI.value == null && existingImageUrl.isNullOrEmpty()) {
            valid = false
        }


        return valid
    }
}
