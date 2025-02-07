package com.example.surfriders.modules.post

import android.net.Uri
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
        if (imageURI.value == null) {
            valid = false
        }

        return valid
    }
}
