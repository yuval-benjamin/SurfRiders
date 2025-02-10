package com.example.surfriders.modules.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.databinding.FragmentFeedBinding

class Feed : Fragment() {

    private lateinit var postAdapter: FeedAdapter
    private lateinit var binding: FragmentFeedBinding

    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(context)

        postAdapter = FeedAdapter()
        binding.recyclerViewFeed.adapter = postAdapter

        feedViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            postAdapter.updatePosts(posts)
        })

        feedViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            feedViewModel.refreshPosts()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("postUpdated") { _, _ ->
            Log.d("FeedFragment", "postUpdated received - refreshing posts")
            feedViewModel.refreshPosts()
        }
    }

}
