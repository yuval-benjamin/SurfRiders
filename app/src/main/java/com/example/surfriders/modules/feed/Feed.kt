package com.example.surfriders.modules.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.databinding.FragmentFeedBinding

class Feed : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: FragmentFeedBinding

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewBinding
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(context)

        // Initialize the PostAdapter
        postAdapter = PostAdapter()
        binding.recyclerViewFeed.adapter = postAdapter

        // Observe posts from the ViewModel
        feedViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            postAdapter.updatePosts(posts)
        })

        return binding.root
    }
}
