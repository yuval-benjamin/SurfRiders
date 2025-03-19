package com.example.surfriders.modules.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel

class SearchViewModel : ViewModel() {
    var posts: MutableLiveData<MutableList<Post>> = MutableLiveData()
    private val model = PostFirebaseModel() // Create instance here

    fun refreshPosts(query: String) {
        model.searchPost(query) {
            posts.postValue(it.toMutableList()) // ensure MutableList
        }
    }

    fun clearPosts() {
        posts.postValue(mutableListOf())
    }
}
