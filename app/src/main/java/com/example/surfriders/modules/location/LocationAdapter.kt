package com.example.surfriders.modules.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R
import com.example.surfriders.data.location.Location
import com.squareup.picasso.Picasso

class LocationAdapter(
    private var locations: List<Location>,
    private val onPostClick: (Location) -> Unit
) :
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
        val addPostButton: Button = itemView.findViewById(R.id.addPostButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.name.text = location.name
        holder.city.text = location.city ?: "Unknown"

        Picasso.get()
            .load(location.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.image)

        holder.addPostButton.setOnClickListener {
            onPostClick(location)
        }
    }


    override fun getItemCount(): Int = locations.size
}
