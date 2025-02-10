package com.example.surfriders.modules.myPosts

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.R
import com.example.surfriders.databinding.FragmentMyPostsBinding

class MyPosts : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: FragmentMyPostsBinding

    private val myPostsViewModel: MyPostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)

        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(context)

        postAdapter = PostAdapter(
            onDeleteClick = { post ->
                myPostsViewModel.deletePost(post.id)
            },
            onUpdateClick = { post ->
                val action = MyPostsDirections.actionMyPostsToUpdatePostFragment(
                    postId = post.id,
                    locationId = post.locationId,
                    locationName = post.locationName,
                    postText = post.text,
                    grade = post.grade,
                    postImageUri = (post.postImage ?: Uri.EMPTY).toString()
                )
                findNavController().navigate(action)
            }
        )
        binding.recyclerViewFeed.adapter = postAdapter

        myPostsViewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            postAdapter.updatePosts(posts)
        })

        myPostsViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        })

        myPostsViewModel.postDeleted.observe(viewLifecycleOwner, Observer { deleted ->
            if (deleted) {
                myPostsViewModel.refreshPosts()
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            myPostsViewModel.refreshPosts()
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_my_profile_to_profile)
        }

        setFragmentResultListener("postUpdated") { _, _ ->
            myPostsViewModel.refreshPosts() // Refresh posts when an update occurs
        }

        return binding.root
    }
}
