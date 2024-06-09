package com.capstone.cleanup.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ActivityMainBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        currentUser = mainViewModel.currentUser
        Log.d("Username Checker", currentUser?.displayName.toString())
        if (currentUser == null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        setSupportActionBar(binding.dummyActionBar)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navView: BottomNavigationView = binding.navMenu
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_zone, R.id.nav_article, R.id.nav_about
            )
        )

        setupActionBarWithNavController(navController, appBarConfig)
        navView.setupWithNavController(navController)
    }
}