package com.example.surfriders.modules.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surfriders.databinding.FragmentLocationBinding
import com.example.surfriders.data.location.Location
import com.example.surfriders.modules.post.AddPostFragment

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocationViewModel by viewModels()
    private lateinit var adapter: LocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LocationAdapter(emptyList()) { location ->
            showAddPostDialog(location)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.locations.observe(viewLifecycleOwner, Observer { locations ->
            adapter.updateList(locations)
            binding.loadingProgressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.fetchLocations()
    }

    private fun showAddPostDialog(location: Location) {
        val bundle = Bundle().apply {
            putString("locationId", location.locationId)
            putString("locationName", location.name)
        }

        val addPostFragment = AddPostFragment().apply {
            arguments = bundle
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, addPostFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
