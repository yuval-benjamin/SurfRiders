package com.example.surfriders.modules.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.surfriders.R
import com.example.surfriders.modules.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var root: View
    private var auth = Firebase.auth
    private val storage = Firebase.storage
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_profile, container, false)
        root = setUI(root)
        return root

    }

    private fun setUI(root: View): View {

        root.findViewById<TextView>(R.id.UserNameTextView).text = "${auth.currentUser?.displayName}"

        val user = auth.currentUser
        if (user != null) {
            val imageRef = storage.reference.child("images/users/${user.uid}")
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(root.findViewById<ImageView>(R.id.ProfileImageView))
            }.addOnFailureListener { exception ->
                Log.d("FirebaseStorage", "Error getting download image URI: $exception")
            }
        } else {
            Log.d("FirebaseAuth", "User is not signed in.")
        }

        root.findViewById<Button>(R.id.MyPostsButton).setOnClickListener {
//            Navigation.findNavController(root).navigate(R.id.) // move to my posts page
        }
        root.findViewById<Button>(R.id.EditProfileButton).setOnClickListener {
//            Navigation.findNavController(root).navigate(R.id.) // move to edit profile
        }
        root.findViewById<Button>(R.id.LogoutButton).setOnClickListener {
            logOut()
        }
        return root

    }

    private fun logOut() {
        auth.signOut()
        Toast.makeText(
            requireContext(),
            "Logged out",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }


}