package com.capstone.cleanup.ui.post

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.cleanup.BuildConfig
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.ActivityPostBinding
import com.capstone.cleanup.utils.getImageUri

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding

    private var currentImageUri: Uri? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_granted))
            } else {
                showToast(getString(R.string.permission_denied))
            }
        }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (currentImageUri != null) {
            outState.putParcelable("photoUri", currentImageUri)
            if (BuildConfig.DEBUG) Log.d(TAG, "$currentImageUri Saved")
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentImageUri = savedInstanceState.getParcelable("photoUri")
        if (BuildConfig.DEBUG) Log.d(TAG, "$currentImageUri Loaded")
    }

    override fun onResume() {
        super.onResume()
        showImage()
    }

    private fun setupAction() {
        binding.btnGallery.setOnClickListener { startGallery() }

        binding.btnCamera.setOnClickListener {
            if (allPermissionGranted()) startCamera()
            else requestCameraPermissionLauncher.launch(CAMERA_PERMISSION)
        }

        binding.btnContinue.setOnClickListener {
            if (currentImageUri != null) {
                val intent = Intent(this, PostActivity2::class.java)
                intent.putExtra(EXTRA_IMAGE, currentImageUri)
                startActivity(intent)
            } else {
                showToast(getString(R.string.empty_image))
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            if (BuildConfig.DEBUG) Log.d("Image URI", "showImage: $it")
            binding.ivPreviewReport.setImageURI(it)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            if (BuildConfig.DEBUG) Log.d("Photo Picker", getString(R.string.empty_image))
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri as Uri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = PostActivity::class.java.simpleName

        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        const val EXTRA_IMAGE = "image for postReport"
    }
}