package com.example.surfriders.modules.post

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.surfriders.R
import com.example.surfriders.databinding.FragmentEditPostBinding
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {

    private lateinit var viewModel: AddPostViewModel
    private var locationId: String? = null
    private var locationName: String? = null
    private var existingImageUrl: String? = null // Store the existing image URL
    private var isNewImageSelected = false // Track if a new image is selected

    private lateinit var imageSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentEditPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationId = arguments?.getString("locationId")
        locationName = arguments?.getString("locationName")
        existingImageUrl = arguments?.getString("postImageUri") // Get the existing image URL
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewBinding
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[AddPostViewModel::class.java]


        locationName?.let {
            binding.textViewPost.text = "Editing post for: $it"
        }

        arguments?.getString("postText")?.let {
            binding.editTextPost.setText(it)
        }

        arguments?.getInt("grade")?.let {
            binding.ratingBar.rating = it.toFloat()
        }

        existingImageUrl.let {
            Picasso.get().load(it)
                .placeholder(R.drawable.profile_logo)
                .into(binding.buttonSelectImage)
        }

        // Define image selection callback
        defineImageSelectionCallBack()

        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageSelectionLauncher.launch(intent)
        }

        binding.cancelButton.invalidate()

        binding.cancelButton.setOnClickListener {
            Log.d("EditPost", "Cancel button clicked")
            findNavController().navigate(R.id.action_editPost_to_MyPosts)
        }

        binding.buttonSubmit.setOnClickListener {
            val postText = binding.editTextPost.text.toString().trim()
            val rating = binding.ratingBar.rating.toInt()

            // Validate inputs
            if (postText.isEmpty()) {
                Toast.makeText(requireContext(), "Post text cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (rating == 0) {
                Toast.makeText(requireContext(), "Grade cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            viewModel.locationName = locationName ?: ""
            viewModel.postText = postText
            viewModel.grade = rating

            viewModel.existingImageUrl = existingImageUrl

            if (isNewImageSelected) {
                if (viewModel.imageURI.value == null) {
                    Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            } else {
                viewModel.imageURI.value = null
            }
//            arguments?.getString("postImageUri")?.let { imageUriString ->
//                val uri = Uri.parse(imageUriString)
//                viewModel.imageURI.value = uri
//            }


            locationId?.let { id ->
                viewModel.savePost(arguments?.getString("postId"), id, {
                    requireActivity().runOnUiThread {
                        setFragmentResult("postUpdated", Bundle())
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }, {
                    // Updated
                    if (isAdded) {
                        setFragmentResult("postUpdated", Bundle()) // Notify MyPosts to refresh
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                })
            }
        }
    }


    private fun defineImageSelectionCallBack() {
        imageSelectionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                try {
                    val imageUri: Uri = result.data?.data!!

                    val imageSize = getImageSize(imageUri)
                    val maxSize = 5 * 1024 * 1024  // 5MB in bytes
                    if (imageSize > maxSize) {
                        Toast.makeText(
                            requireContext(),
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.imageURI.postValue(imageUri)
                        binding.buttonSelectImage.setImageURI(imageUri)
                        isNewImageSelected = true
                    }
                } catch (e: Exception) {
                    Log.d("EditPostFragment", "Error selecting image: $e")
                    Toast.makeText(requireContext(), "Error selecting image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun getImageSize(uri: Uri): Long {
        var size = 0L
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            size = inputStream?.available()?.toLong() ?: 0L
            inputStream?.close()
        } catch (e: Exception) {
            Log.d("EditPostFragment", "Error getting file size: $e")
        }
        return size
    }
}
