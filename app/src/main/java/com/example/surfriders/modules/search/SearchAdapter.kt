package com.example.surfriders.modules.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post

class SearchAdapter(
    var posts: MutableList<Post>?,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<SearchHolder>() {

    override fun getItemCount(): Int = posts?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_search_card, parent, false)
        return SearchHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val post = posts?.get(position)
        if (post != null) {
            holder.bind(post)
            holder.itemView.setOnClickListener { onPostClick(post) }
        }
    }
}

