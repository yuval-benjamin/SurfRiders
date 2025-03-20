package com.example.surfriders.modules.postDetails

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.databinding.ItemPostDetailBinding
import com.example.surfriders.data.post.Post
import com.squareup.picasso.Picasso

class PostDetailAdapter : RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder>() {

    private var post: Post? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setPost(newPost: Post?) {
        post = newPost
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailViewHolder {
        val binding = ItemPostDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostDetailViewHolder, position: Int) {
        holder.bind(post)
    }

    override fun getItemCount(): Int = if (post != null) 1 else 0

    class PostDetailViewHolder(private val binding: ItemPostDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post?) {
            if (post == null) return

            binding.textViewUsername.text = post.username
            binding.textViewPost.text = post.text
            binding.textViewLocation.text = post.locationName
            binding.ratingBar.rating = post.grade.toFloat()
            Picasso.get()
                .load(post.userProfileImage)
                .into(binding.imageViewUserProfile)
            Picasso.get()
                .load(post.postImage)
                .into(binding.imageViewPostImage)
        }
    }
}
