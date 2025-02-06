package com.example.surfriders.modules.location

import android.os.Bundle
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
import com.example.surfriders.data.location.LocationService
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
        adapter = LocationAdapter(emptyList())
        recyclerView.adapter = adapter
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        fetchLocations()
    }

    private fun fetchLocations() {
        loadingProgressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val locations = LocationService.instance.getLocations()

                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                adapter.updateList(locations)  // ✅ Now locations is a List<Location>
            } catch (e: Exception) {

                loadingProgressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to load locations", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
