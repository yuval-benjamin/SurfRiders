package com.example.surfriders.modules.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.surfriders.R
import com.example.surfriders.data.location.Location

class LocationAdapter(private var locations: List<Location>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newLocations: List<Location>) {
        locations = newLocations
        notifyDataSetChanged()  // Notify adapter to refresh UI
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.locationName)
        val city: TextView = itemView.findViewById(R.id.locationCity)
        val image: ImageView = itemView.findViewById(R.id.locationImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.name.text = location.name
        holder.city.text = location.city ?: "Unknown"

        Glide.with(holder.itemView.context)
            .load(location.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.image)
    }

    override fun getItemCount(): Int = locations.size
}
