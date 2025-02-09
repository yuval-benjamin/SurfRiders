package com.example.surfriders.data.post

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage

class PostFirebaseModel {
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val POSTS_COLLECTION_PATH = "posts"
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings { })
        }
        db.firestoreSettings = settings
    }

    fun getAllPosts(callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val posts: MutableList<Post> = mutableListOf()
                    for (json in it.result) {
                        val post = Post.fromJSON(json.data)
                        posts.add(post)
                    }
                    callback(posts)
                } else {
                    callback(listOf())
                }
            }
    }


    fun getImage(imageId: String, callback: (Uri) -> Unit) {
        storage.reference.child("images/$POSTS_COLLECTION_PATH/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri -> callback(uri) }
    }

    fun addPostImage(postId: String, selectedImageUri: Uri, callback: (Uri) -> Unit) {
        val imageRef = storage.reference.child("images/$POSTS_COLLECTION_PATH/$postId")
        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PostFirebaseModel", "Error uploading image: ${exception.message}")
            }
    }


    fun addPost(post: Post, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH).document(post.id).set(post.json)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("PostFirebaseModel", "Error adding post: ${exception.message}")
            }
    }
}