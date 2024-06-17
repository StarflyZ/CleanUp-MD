package com.capstone.cleanup.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.cleanup.databinding.ActivityRegisterBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.login.LoginActivity
import com.capstone.cleanup.utils.showLoading
import com.capstone.cleanup.utils.showToast

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        observeLiveData()

        with(binding){

            btnRegister.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                val repass = rePassEditText.text.toString().trim()
                if (isValidInput(name, email, password, repass)) {
                    registerViewModel.register(email, password, name)
                } else {
                    Toast.makeText(this@RegisterActivity, "Please enter valid details!", Toast.LENGTH_SHORT).show()
                }
            }

            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
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
        return binding.emailEditText.isValidEmail(email)
    }

    private fun isPasswordValid(password: String): Boolean {
        return binding.passwordEditText.isValidPassword(password)
    }

    private fun observeLiveData() {
        registerViewModel.isSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(binding.progressBar, it)
        }

        registerViewModel.errMsg.observe(this) {
            showToast(this, it)
        }
    }
}