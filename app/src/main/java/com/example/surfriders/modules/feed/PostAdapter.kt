package com.example.surfriders.modules.feed

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.example.surfriders.data.post.PostFirebaseModel
import com.example.surfriders.databinding.ItemPostBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts: MutableList<Post> = mutableListOf()
    private val postFirebaseModel = PostFirebaseModel()

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosts(newPosts: List<Post>) {
        Log.d("PostAdapter", "Updating posts: ${newPosts.size}")
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.textViewPost.text = post.text
            binding.ratingBar.rating = post.grade.toFloat()
            binding.textViewLocation.text = post.locationName

            Log.d("PostAdapter", "Image ID: ${post.postImage}")

            // Check if the post image URL is valid
            post.postImage?.let { imageUrl ->
                Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(binding.imageViewPostImage)
            }
        }
    }
}
