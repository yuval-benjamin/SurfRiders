package com.example.surfriders.modules.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.surfriders.R

class Feed : Fragment() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = root.findViewById(R.id.recyclerViewFeed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the PostAdapter without arguments
        postAdapter = PostAdapter()

        recyclerView.adapter = postAdapter

        return root
    }
}
