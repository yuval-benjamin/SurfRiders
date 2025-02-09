package com.example.surfriders.modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.surfriders.MainActivity
import com.example.surfriders.R
import com.example.surfriders.modules.feed.Feed
import com.example.surfriders.modules.signup.SignUpActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

//    private lateinit var auth: FirebaseAuth
    private var auth = Firebase.auth
    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressInputEditText: TextInputEditText


    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordInputEditText: TextInputEditText

    private lateinit var signUpButton: Button
    private lateinit var signinButton: Button
    private lateinit var forgotPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("creation", "creating signing screen")
        setContentView(R.layout.login_screen)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.i("creation", "User is already signed in, navigating to feed")
            loggedInHandler()
        }

        setUI()
    }

    private fun setUI() {
        signinButton = findViewById(R.id.LoginButton)
        forgotPasswordButton = findViewById(R.id.ForgotPasswordPageButton)
        signUpButton = findViewById(R.id.toSignupPageButton)

        signinButton.setOnClickListener {
            Log.i("buttonClick", "signing button clicked in login page")
            checkLoginUser()
        }

        signUpButton.setOnClickListener {
            Log.i("buttonClick", "signup button in signing screen clicked")
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        forgotPasswordButton.setOnClickListener {
            Log.i("buttonClick", "Forgot password button in signing screen clicked")
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLoginUser() {

        emailAddressInputLayout = findViewById(R.id.layoutLoginTextEmail)
        emailAddressInputEditText = findViewById(R.id.editLoginTextEmailAddress)
        val emailValue: String = emailAddressInputEditText.text.toString().trim()

        passwordInputEditText = findViewById(R.id.editLoginTextPassword)
        passwordInputLayout = findViewById(R.id.layoutLoginTextPassword)
        val passwordValue: String = passwordInputEditText.text.toString().trim()

        val checkUserValidation = loginUserValidation(emailValue, passwordValue)

        if (checkUserValidation) {
            Log.i("buttonClick", "signIn button in signing screen clicked")
            Log.i("signingSubmit", "email input is: $emailValue")
            Log.i("signingSubmit", "password Input is: $passwordValue")

            auth.signInWithEmailAndPassword(emailValue, passwordValue).addOnSuccessListener {
                loggedInHandler()
            }.addOnFailureListener {
                Toast.makeText(
                    this@LoginActivity,
                    "Your Email or Password is incorrect!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loggedInHandler() {
        Toast.makeText(
            this@LoginActivity,
            "Welcome ${auth.currentUser?.displayName}!",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginUserValidation(
        email: String,
        password: String
    ): Boolean {
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
        } else {
            passwordInputLayout.error = null
        }
        return true

    }
}