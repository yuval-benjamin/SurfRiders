
package com.example.surfriders.modules.editProfile
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.surfriders.R
import com.example.surfriders.databinding.FragmentEditProfileBinding
import com.squareup.picasso.Picasso


class EditProfile : Fragment() {
    private lateinit var root: View
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditProfileViewModel

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val imageUri: Uri = result.data?.data!!
                val imageSize = getImageSize(imageUri)
                val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                if (imageSize > maxCanvasSize) {
                    Toast.makeText(
                        requireContext(),
                        "Selected image is too large",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.selectedImageURI.postValue(imageUri)
                    viewModel.imageChanged = true
                    binding.profilePicButton.setImageURI(imageUri)
                }
            } catch (e: Exception) {
                Log.d("EditMyReview", "Error: $e")
                Toast.makeText(
                    requireContext(), "Error processing result", Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        setUI()
        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun setUI() {
        viewModel.loadUser()
        binding.editTextFirstName.addTextChangedListener {
            viewModel.firstName = it.toString().trim()
        }
        binding.editTextLastName.addTextChangedListener {
            viewModel.lastName = it.toString().trim()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.editTextFirstName.setText(user.firstName)
                binding.editTextLastName.setText(user.lastName)
            } else {
                Log.e("EditProfile", "User is null!")
            }
        }

        viewModel.selectedImageURI.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(binding.profilePicButton)
        }

        viewModel.firstNameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.editTextFirstName.error = it
        }
        viewModel.lastNameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.editTextLastName.error = it
        }

        binding.saveButton.setOnClickListener {
            binding.saveButton.isClickable = false
            viewModel.updateUser {
                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_edit_profile_to_profile)
                binding.saveButton.isClickable = true
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_edit_profile_to_profile)
        }

        binding.profilePicButton.setOnClickListener {
            defineImageSelectionCallBack()
        }


    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun defineImageSelectionCallBack() {
        binding.profilePicButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionLauncher.launch(intent)
        }
    }

}