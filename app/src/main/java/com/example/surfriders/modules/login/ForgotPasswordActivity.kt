package com.example.surfriders.modules.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.surfriders.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var sendEmailButton: Button
    private lateinit var rememberPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("creation", "creating forgot password screen")
        setContentView(R.layout.forgot_password_screen)

        setUI()

    }
   private fun setUI() {

        rememberPassword = findViewById(R.id.ForgotPasswordRememberButton)
        sendEmailButton = findViewById(R.id.ForgotPasswordEmailLinkButton)
       rememberPassword.setOnClickListener {
           Log.i("buttonClick", "Remember Password button in forgot password screen clicked")
           val intent = Intent(this, LoginActivity::class.java)
           startActivity(intent)
           finish()
       }

        sendEmailButton.setOnClickListener {
            Log.i("buttonClick", "Send email button in forgot password screen clicked")
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}