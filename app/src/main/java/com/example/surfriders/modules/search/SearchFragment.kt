package com.example.surfriders.modules.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.example.surfriders.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var searchRecyclerView: RecyclerView? = null
    private var _binding: FragmentSearchBinding? = null
    private var adapter: SearchAdapter? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        searchRecyclerView = binding.searchResultsLayout
        searchRecyclerView?.setHasFixedSize(true)
        searchRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = SearchAdapter(viewModel.posts.value) { post ->
            Log.d("SearchFragment", "Clicked post ID: ${post.id}") // Debug log
            val action = SearchFragmentDirections.actionSearchFragmentToPostDetailFragment(postId = post.id)
            findNavController().navigate(action)
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.clearPosts()
                viewModel.refreshPosts(query)
                binding.SearchTextView.visibility = View.GONE
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (viewModel.posts.value?.isNotEmpty() == true)
                    binding.SearchTextView.visibility = View.GONE
                else
                    binding.SearchTextView.visibility = View.VISIBLE
                return false
            }
        })

        searchRecyclerView?.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) {
            Log.d("SearchFragment", "Observer triggered, post size: ${it?.size}")
            adapter?.posts = it
            adapter?.notifyDataSetChanged()
        }

        return view
    }
}