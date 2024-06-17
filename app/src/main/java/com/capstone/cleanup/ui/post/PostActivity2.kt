package com.capstone.cleanup.ui.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.cleanup.R
import com.capstone.cleanup.data.Reports
import com.capstone.cleanup.databinding.ActivityPost2Binding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.main.MainActivity
import com.capstone.cleanup.utils.showLoading
import java.util.Date

class PostActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityPost2Binding

    private val postViewModel by viewModels<PostViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPost2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgUri: Uri? = intent.getParcelableExtra(PostActivity.EXTRA_IMAGE)
        postViewModel.uploadPicAndGetUrl(imgUri!!)

        var imgUrl: String? = null

        with(binding) {
            postViewModel.isLoading.observe(this@PostActivity2) {
                showLoading(progressBar, it)
            }

            postViewModel.isSuccess.observe(this@PostActivity2) {
                if (it) {
                    startActivity(Intent(this@PostActivity2, MainActivity::class.java))
                }
            }

            postViewModel.imgUrl.observe(this@PostActivity2) {
                imgUrl = it.toString()
            }

            btnSubmitReport.setOnClickListener {
                val title = edTitle.text.toString().trim()
                val desc = edDesc.text.toString().trim()
                val name = postViewModel.username
                val loc = edLoc.text.toString().trim()

                val postReport = Reports(
                    desc,
                    imgUrl,
                    name,
                    title,
                    Date().time,
                    loc
                )

                postViewModel.addNewReport(postReport)
            }
        }
    }
}