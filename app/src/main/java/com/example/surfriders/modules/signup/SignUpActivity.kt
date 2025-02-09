package com.example.surfriders.modules.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import com.example.surfriders.MainActivity
import com.example.surfriders.R
import com.example.surfriders.data.user.User
import com.example.surfriders.data.user.UserModel
import com.example.surfriders.modules.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var imageURI: Uri? = null

    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var firstNameEditText: TextInputEditText

    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var lastNameInputEditText: TextInputEditText

    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressInputEditText: TextInputEditText


    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordInputEditText: TextInputEditText


    private lateinit var passwordConfirmationInputLayout: TextInputLayout
    private lateinit var passwordConfirmationInputEditText: TextInputEditText


    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button

    private lateinit var pickProfilePictureButton: ImageButton

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("creation", "creating signup screen")
        setContentView(R.layout.signup_screen)

        backToLogin()
        setUI()

    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    fun setUI() {
        signUpButton = findViewById(R.id.SignupButton)
        pickProfilePictureButton = findViewById(R.id.profilePicButtonSignUpScreen)
        signInButton = findViewById(R.id.AlreadyHaveAccountButton)

        // Define all button listeners
        defineImageSelectionCallBack()
        signUpButton.setOnClickListener {
            checkNewUserDetails()
        }
        signInButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        pickProfilePictureButton.setOnClickListener {
            Log.i("buttonClick", "Pick profile pick button in signup screen clicked")
            openGallery()

        }
    }

    private fun checkNewUserDetails() {
        firstNameEditText = findViewById(R.id.editTextFirstName)
        firstNameInputLayout = findViewById(R.id.layoutTextFirstName)
        val firstNameValue = firstNameEditText.text.toString().trim()

        lastNameInputEditText = findViewById(R.id.editTextLastName)
        lastNameInputLayout = findViewById(R.id.layoutTextLastName)
        val lastNameValue = lastNameInputEditText.text.toString().trim()

        emailAddressInputEditText = findViewById(R.id.editTextEmail)
        emailAddressInputLayout = findViewById(R.id.layoutTextEmail)
        val emailValue = emailAddressInputEditText.text.toString().trim()

        passwordInputEditText = findViewById(R.id.editTextPassword)
        passwordInputLayout = findViewById(R.id.layoutTextPassword)
        val passwordValue = passwordInputEditText.text.toString().trim()

        passwordConfirmationInputEditText = findViewById(R.id.editTextPasswordConfirm)
        passwordConfirmationInputLayout = findViewById(R.id.layoutTextPasswordConfirm)
        val passwordConfirmationValue = passwordConfirmationInputEditText.text.toString().trim()

        val checkUserValidation = userValidation(
            firstNameValue,
            lastNameValue,
            emailValue,
            passwordValue,
            passwordConfirmationValue
        )

        if (checkUserValidation) {
            Log.i("buttonClick", "Signup button in signup screen clicked")
            Log.i("signupSubmit", "First Name Input is:$firstNameValue")
            Log.i("signupSubmit", "Last Name Input is:$lastNameValue")
            Log.i("signupSubmit", "Email input is:$emailValue")
            Log.i("signupSubmit", "Password Input is:$passwordValue")
            Log.i("signupSubmit", "Password Confirmation Input is:$passwordConfirmationValue")
            auth.createUserWithEmailAndPassword(emailValue, passwordValue).addOnSuccessListener {
                val authenticatedUser = it.user!!

                val profileChange = UserProfileChangeRequest.Builder()
                    .setPhotoUri(imageURI)
                    .setDisplayName("$firstNameValue $lastNameValue")
                    .build()

                authenticatedUser.updateProfile(profileChange)

                UserModel.instance.addUser(
                    User(authenticatedUser.uid, firstNameValue, lastNameValue),
                    imageURI!!
                ) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Register Successful",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@SignUpActivity,
                    "Register Failed, " + it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun userValidation(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty()) {
            firstNameInputLayout.error = "First name cannot be empty"
            return false
        } else {
            firstNameInputLayout.error = null
        }
        if (lastName.isEmpty()) {
            lastNameInputLayout.error = "Last name cannot be empty"
            return false
        } else {
            lastNameInputLayout.error = null
        }
        if (email.isEmpty()) {
            emailAddressInputLayout.error = "Email cannot be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddressInputLayout.error = "Invalid email format"
            return false
        } else {
            emailAddressInputLayout.error = null
        }
        if (password.isEmpty()) {
            passwordInputLayout.error = "Password cannot be empty"
            return false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            return false
        } else if (!password.any { it.isDigit() }) {
            passwordInputLayout.error = "Password must contain at least one digit"
            return false
        } else {
            passwordInputLayout.error = null
        }
        if (confirmPassword.isEmpty()) {
            passwordConfirmationInputLayout.error = "Confirm password cannot be empty"
            return false
        } else if (password != confirmPassword) {
            passwordConfirmationInputLayout.error = "Passwords do not match"
            return false
        } else {
            passwordConfirmationInputLayout.error = null
        }
        if (imageURI == null) {
            Toast.makeText(
                this@SignUpActivity,
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun backToLogin() {
        findViewById<TextView>(R.id.AlreadyHaveAccountButton).setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionCallBack.launch(intent)
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                try {
                    val imageUri: Uri? = result.data?.data
                    if (imageUri != null) {
                        val imageSize = getImageSize(imageUri)
                        val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                        if (imageSize > maxCanvasSize) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Selected image is too large",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            pickProfilePictureButton.setImageURI(imageUri)
                            imageURI = imageUri
                        }

                    } else {
                        Toast.makeText(this@SignUpActivity, "No Image Selected", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Error processing result",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}