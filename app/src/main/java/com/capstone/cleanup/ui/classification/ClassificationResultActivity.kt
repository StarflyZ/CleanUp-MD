package com.capstone.cleanup.ui.classification

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ActivityClassificationResultBinding

class ClassificationResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClassificationResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityClassificationResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val result = intent.getStringExtra(ClassificationFragment.EXTRA_RESULT)
        val image: Uri? = intent.getParcelableExtra(ClassificationFragment.EXTRA_IMAGE)

        binding.ivPreviewClassify.setImageURI(image)
        binding.tvClassificationResult.text = getString(R.string.classify_result, result)
    }
}