package com.capstone.cleanup.ui.classification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.capstone.cleanup.BuildConfig
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.FragmentClassificationBinding
import com.capstone.cleanup.utils.ImageClassifierHelper
import com.capstone.cleanup.utils.getImageUri
import com.capstone.cleanup.utils.showLoading
import com.capstone.cleanup.utils.showToast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.text.NumberFormat

class ClassificationFragment : Fragment() {
    private var _binding: FragmentClassificationBinding? = null
    private val binding get() = _binding

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null

    private var displayResult: String = ""
    private var resultValue: String = ""
    private var score: String = ""

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(requireActivity(), getString(R.string.permission_granted))
            } else {
                showToast(requireActivity(), getString(R.string.permission_denied))
            }
        }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClassificationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (currentImageUri != null) {
            outState.putParcelable("photoUri", currentImageUri)
            if (BuildConfig.DEBUG) Log.d(TAG, "$currentImageUri Saved")
        }

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        currentImageUri = savedInstanceState?.getParcelable("photoUri")
        if (BuildConfig.DEBUG) Log.d(TAG, "$currentImageUri Loaded")
    }

    override fun onResume() {
        super.onResume()
        showImage()
    }

    private fun showImage() {
        currentImageUri?.let {
            if (BuildConfig.DEBUG) Log.d("Image URI", "showImage: $it")
            binding?.ivPreviewClassify?.setImageURI(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private fun setupAction() {
        binding?.apply {
            btnGallery.setOnClickListener { startGallery() }

            btnCamera.setOnClickListener {
                if (allPermissionGranted()) startCamera()
                else requestCameraPermissionLauncher.launch(CAMERA_PERMISSION)
            }

            btnContinue.setOnClickListener {
                showLoading(progressBar, true)
                val dispatcher = newSingleThreadContext("myThread")
                currentImageUri?.let { uri ->
                    lifecycleScope.launch(dispatcher) { analyzeImage() }
                    lifecycleScope.launch(dispatcher) { imageClassifierHelper.classifyStaticImage(uri) }
                } ?: run {
                    showToast(requireActivity(), getString(R.string.empty_image))
                }
            }
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
        currentImageUri = getImageUri(requireActivity())
        launcherCamera.launch(currentImageUri as Uri)
    }

    private fun moveToResult() {
        val intent = Intent(requireActivity(), ClassificationResultActivity::class.java)
        intent.putExtra(EXTRA_RESULT, displayResult)
        intent.putExtra(EXTRA_IMAGE, currentImageUri)
        intent.putExtra(EXTRA_RESULT_VALUE, resultValue)
        startActivity(intent)
    }

    private fun analyzeImage() {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireActivity(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    requireActivity().runOnUiThread {
                        showLoading(binding?.progressBar!!, false)
                        showToast(requireActivity(), error)
                    }
                }

                override fun onResult(result: List<Classifications>?) {
                    requireActivity().runOnUiThread {
                        result?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                if (BuildConfig.DEBUG) Log.d(TAG, it.toString())
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                displayResult = sortedCategories[0].let {
                                    resultValue = when (it.label) {
                                        "U" -> "Inorganic "
                                        "O" -> "Organic "
                                        else -> "Other type"
                                    }
                                    score =
                                        NumberFormat.getPercentInstance().format(it.score).trim()
                                    resultValue + score
                                }
                                showLoading(binding?.progressBar!!, false)
                                moveToResult()
                            } else {
                                displayResult = getString(R.string.result_empty)
                                showLoading(binding?.progressBar!!, false)
                                moveToResult()
                            }
                        }
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private val TAG = ClassificationFragment::class.java.simpleName

        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        const val EXTRA_IMAGE = "image for classification result"
        const val EXTRA_RESULT = "result for ml"
        const val EXTRA_RESULT_VALUE = "result value for ml"
    }
}