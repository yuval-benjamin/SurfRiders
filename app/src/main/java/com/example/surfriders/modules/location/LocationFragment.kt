package com.example.surfriders.modules.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.location.Location
import com.example.surfriders.data.location.LocationService
import com.example.surfriders.modules.post.AddPostFragment
import kotlinx.coroutines.launch

class LocationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LocationAdapter(emptyList()) { location ->
            showAddPostDialog(location)
        }

        recyclerView.adapter = adapter
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        fetchLocations()
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
            .replace(R.id.fragmentContainerView, addPostFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun addPostForLocation(location: Location, postContent: String) {
        Toast.makeText(requireContext(), "Post added for ${location.name}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun fetchLocations() {
        loadingProgressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val locations = LocationService.instance.getLocations()
                if (locations.isEmpty()) {
                    Log.e("LocationFragment", "No locations returned")
                }

                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter.updateList(locations)
            } catch (e: Exception) {
                e.printStackTrace()
                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to load locations", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
