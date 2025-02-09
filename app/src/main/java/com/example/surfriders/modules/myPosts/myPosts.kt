package com.example.surfriders.modules.myPosts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.R
import com.example.surfriders.databinding.FragmentMyPostsBinding

class myPosts : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: FragmentMyPostsBinding

    private val myPostsViewModel: myPostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)

        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter()
        binding.recyclerViewFeed.adapter = postAdapter

        myPostsViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            postAdapter.updatePosts(posts)
        })

        myPostsViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            myPostsViewModel.refreshPosts()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_my_profile_to_profile)
        }

        return binding.root
    }
}
