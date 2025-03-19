package com.example.surfriders.data.post

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.storage.storage
import com.google.firebase.auth.auth


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

    fun getPost(postId: String, callback: (Post?) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(postId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val post = it.result!!.data?.let { it1 -> Post.fromJSON(it1) }
                    callback(post)
                } else {
                    callback(null)
                }
            }
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

    fun getUserPosts(callback: (List<Post>) -> Unit) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser == null) {
            callback(listOf())
            return
        }

        db.collection(POSTS_COLLECTION_PATH)
            .whereEqualTo("userId", currentUser.uid)
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

    fun deletePost(postId: String, callback: (Boolean) -> Unit) {
        val postRef = db.collection(POSTS_COLLECTION_PATH).document(postId)
        val imageRef = storage.reference.child("images/$POSTS_COLLECTION_PATH/$postId")
        postRef.delete()
            .addOnSuccessListener {
                Log.d("RONNE", "trying to delete image")
                imageRef.delete()
                    .addOnSuccessListener {
                        callback(true) // Post and image deleted successfully
                    }
                    .addOnFailureListener {
                        callback(false) // Failed to delete the image
                    }
            }
            .addOnFailureListener {
                callback(false) // Failed to delete the post
            }
    }

    fun updatePost(post: Post?, callback: () -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .document(post!!.id).update(post.updateJson)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                Log.d("Error", "Can't update this post document: " + it.message)
            }
    }

    fun searchPost(query: String, callback: (List<Post>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val posts = mutableListOf<Post>()
                    val lowerQuery = query.lowercase()
                    for (doc in task.result) {
                        val post = Post.fromJSON(doc.data)
                        if (post.text.lowercase().contains(lowerQuery) ||
                            post.locationName.lowercase().contains(lowerQuery)
                        ) {
                            posts.add(post)
                        }
                    }
                    callback(posts)
                } else {
                    callback(listOf())
                }
            }
    }
}