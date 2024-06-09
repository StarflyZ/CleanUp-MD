package com.capstone.cleanup.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.cleanup.databinding.ActivityLoginBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.main.MainActivity
import com.capstone.cleanup.ui.register.RegisterActivity
import com.capstone.cleanup.utils.showLoading

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        loginViewModel.isSuccess.observe(this) {
            if (it) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(binding.progressBar, it)
        }

        with(binding){
            tvReg.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }

            signInButton.setOnClickListener {
                loginViewModel.signIn()
            }

            btnLogin.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Please enter email and password", Toast.LENGTH_SHORT).show()
                } else {
                    loginViewModel.loginWithEmailAndPassword(email, password)
                }
            }
        }
    }
}