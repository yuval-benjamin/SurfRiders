package com.example.surfriders.modules.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel
import com.squareup.picasso.Picasso

class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val postImageView: ImageView = itemView.findViewById(R.id.postResultImage)
    private val postTitleView: TextView = itemView.findViewById(R.id.postResultTitle)
//    private val postLocationView: TextView = itemView.findViewById(R.id.postResultLocation)

    fun bind(post: Post?) {
        if (post == null) return

        postTitleView.text = post.text // Show post text
//        postLocationView.text = post.locationName // Show location name

        // Load post image from Firebase Storage
        PostFirebaseModel().getImage(post.id) { uri ->
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.placeholder_image) // optional: a default image while loading
                .into(postImageView)
        }
    }
}