package com.example.surfriders.modules.feed

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts: MutableList<Post> = mutableListOf()
    private val postFirebaseModel = PostFirebaseModel()

    init {
        // Fetch posts from Firebase when the adapter is created
        fetchPosts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPosts() {
        // Assuming you want all posts or to fetch based on a specific timestamp
        val since = Post.lastUpdated
        postFirebaseModel.getAllPosts(since) { postList ->
            posts.clear()
            posts.addAll(postList)
            notifyDataSetChanged()  // Update the adapter with the new list of posts
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPost: TextView = itemView.findViewById(R.id.textViewPost)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
        private val imageViewPostImage: ImageView = itemView.findViewById(R.id.imageViewPostImage)

        fun bind(post: Post) {
            textViewPost.text = post.text
            ratingBar.rating = post.grade.toFloat()
            textViewLocation.text = post.locationName

            // Load image from Firebase
            post.postImage?.let { imageId ->
                postFirebaseModel.getImage(imageId) { uri ->
                    Picasso.get().load(uri).into(imageViewPostImage)
                }
            }
        }
    }
}
