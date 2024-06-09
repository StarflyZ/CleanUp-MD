package com.capstone.cleanup.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ActivityDetailArticleBinding

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = intent.getStringExtra(EXTRA_TITLE)
        val content = intent.getStringExtra(EXTRA_CONTENT)
        val img = intent.getStringExtra(EXTRA_IMGURL)

        binding.apply {
            tvArticleTitle.text = title
            tvArticleDesc.text = content

            Glide.with(this@DetailArticleActivity)
                .load(img)
                .fitCenter()
                .into(imageView)
        }

    }

    companion object {

        const val EXTRA_TITLE = "extra title"
        const val EXTRA_IMGURL = "extra img url"
        const val EXTRA_CONTENT = "extra content"
    }
}