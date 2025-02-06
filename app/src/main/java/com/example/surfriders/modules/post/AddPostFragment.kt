package com.example.surfriders.modules.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.surfriders.R
import com.example.surfriders.data.post.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.UUID

class AddPostFragment : Fragment() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var locationId: String? = null
    private var locationName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationId = arguments?.getString("locationId")
        locationName = arguments?.getString("locationName")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_post, container, false)

        val editTextPost = root.findViewById<EditText>(R.id.editTextPost)
        val editTextGrade = root.findViewById<EditText>(R.id.editTextGrade)
        val buttonSubmit = root.findViewById<Button>(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val text = editTextPost.text.toString().trim()
            val grade = editTextGrade.text.toString().toIntOrNull() ?: 0
            val userId = auth.currentUser?.uid ?: return@setOnClickListener
            val locationId = arguments?.getString("locationId") ?: ""

            val newPost = Post(
                id = UUID.randomUUID().toString(),
                text = text,
                grade = grade,
                userId = userId,
                locationId = locationId
            )

            savePostToFirebase(newPost)
        }

        return root
    }

    private fun savePostToFirebase(post: Post) {
        db.collection("posts").document(post.id).set(post)
            .addOnSuccessListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}