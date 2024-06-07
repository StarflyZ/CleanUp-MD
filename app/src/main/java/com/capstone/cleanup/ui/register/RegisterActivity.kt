package com.capstone.cleanup.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ActivityRegisterBinding
import com.capstone.cleanup.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        supportActionBar?.hide()

        with(binding){

            btnRegister.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val repass = rePassEditText.text.toString()
                if (isValidInput(name, email, password, repass)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@RegisterActivity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                                Toast.makeText(this@RegisterActivity, "Registration failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this@RegisterActivity, "Please enter valid details!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    private fun isValidInput(name: String, email: String, password: String, repassword: String): Boolean {
        if (name.isEmpty()) {
            return false
        }

        if (!isValidEmail(email)) {
            return false
        }

        if (!isPasswordValid(password)) {
            return false
        }

        return password == repassword // Check if passwords match
    }

    // Helper methods for email and password validation (implement these)
    private fun isValidEmail(email: String): Boolean {
        // Implement email address validation logic here
        // You can use regular expressions or other techniques
        return true // Replace with your implementation
    }

    private fun isPasswordValid(password: String): Boolean {
        // Implement password validation logic here
        // You can check password length, complexity (uppercase, lowercase, symbols)
        return true // Replace with your implementation
    }

    companion object{
        const val TAG = "RegisterActivity"
    }
}