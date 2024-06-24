package com.ksc.weatherapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ksc.weatherapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.btnSignUp.setOnClickListener {
            val name = binding.etNameSignup.text.toString()
            val email = binding.etEmailSignup.text.toString()
            val password = binding.etPasswordSignup.text.toString()
            val confirmPassword = binding.etConfirmPasswordSignup.text.toString()

            //Check all the fields are filled
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all the fields", Snackbar.LENGTH_SHORT)
            } else if (password != confirmPassword) {

                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
            } else {
                //Create a user in Firebase
                registerUser(email, password)

            }
        }
        binding.tvLogIn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Snackbar.make(binding.root, "Account created successfully", Snackbar.LENGTH_SHORT)
                    .show()
                auth.signOut()
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }

    }
}