package com.example.surfriders.modules.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {
    private var postId: String? = null
    private lateinit var viewModel: PostDetailViewModel
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = arguments?.getString("postId")
        viewModel = ViewModelProvider(this)[PostDetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostDetailAdapter()
        binding.recyclerViewPostDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPostDetail.adapter = adapter

        viewModel.loadPost(postId)

        viewModel.post.observe(viewLifecycleOwner) { post ->
            Log.d("SearchFragment", "POST : $post")
            adapter.setPost(post)
        }
    }
}
