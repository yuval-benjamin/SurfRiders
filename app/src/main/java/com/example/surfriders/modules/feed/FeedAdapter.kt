package com.example.surfriders.modules.feed

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.example.surfriders.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.PostViewHolder>() {

    private val posts: MutableList<Post> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosts(newPosts: List<Post>) {
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
            binding.textViewUsername.text = post.username ?: "Unknown User"

            post.userProfileImage?.let { imageUrl ->
                Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.profile_logo)
                    .into(binding.imageViewUserProfile)
            }

            post.postImage?.let { imageUrl ->
                Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(binding.imageViewPostImage)
            }
        }
    }

}
