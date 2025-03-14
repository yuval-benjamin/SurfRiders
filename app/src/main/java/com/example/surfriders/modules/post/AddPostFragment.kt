package com.example.surfriders.modules.post

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.surfriders.R
import com.example.surfriders.databinding.FragmentAddPostBinding

class AddPostFragment : Fragment() {

    private lateinit var viewModel: AddPostViewModel
    private var locationId: String? = null
    private var locationName: String? = null

    private lateinit var imageSelectionLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentAddPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationId = arguments?.getString("locationId")
        locationName = arguments?.getString("locationName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[AddPostViewModel::class.java]

        defineImageSelectionCallBack()

        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageSelectionLauncher.launch(intent)
        }

        binding.cancelButton.setOnClickListener {
            Log.d("AddPost", "Cancel button clicked")
            findNavController().navigate(R.id.action_addPostFragment_to_surfFragment)
        }

        binding.buttonSubmit.setOnClickListener {
            val postText = binding.editTextPost.text.toString().trim()

            // Validate inputs
            if (postText.isEmpty()) {
                Toast.makeText(requireContext(), "Post text cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val grade = binding.ratingBar.rating.toInt()

            if (grade == 0) {
                Toast.makeText(requireContext(), "Please rate the post", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (viewModel.imageURI.value == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.locationName = locationName ?: ""
            viewModel.postText = postText
            viewModel.grade = grade

            locationId?.let { id ->
                viewModel.savePost(null, id, {
                    // Created
                    requireActivity().supportFragmentManager.popBackStack()
                }, {
                    // Updated
                    requireActivity().supportFragmentManager.popBackStack()
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
                    }
                } catch (e: Exception) {
                    Log.d("AddPostFragment", "Error selecting image: $e")
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
            Log.d("AddPostFragment", "Error getting file size: $e")
        }
        return size
    }
}
