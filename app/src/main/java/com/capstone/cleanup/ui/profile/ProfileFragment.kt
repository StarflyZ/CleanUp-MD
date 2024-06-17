package com.capstone.cleanup.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.cleanup.BuildConfig
import com.capstone.cleanup.R
import com.capstone.cleanup.databinding.FragmentProfileBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.home.HomeActivity
import com.capstone.cleanup.utils.showLoading

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private lateinit var navController: NavController

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var imgUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imgUri = uri
        } else {
            if (BuildConfig.DEBUG) Log.d("Photo Picker", getString(R.string.empty_image))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.isUploading.observe(requireActivity()) {
            showLoading(binding?.progressBar!!, it)
        }

        showProfilePic()

        profileViewModel.profilePicLive.observe(requireActivity()) {
           showProfilePic(it)
        }


        Log.d("ProfilePicChecker", profileViewModel.profilePic.toString())
        binding?.apply {
            tvUsername.text = profileViewModel.username

            ivProfilePic.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnLogout.setOnClickListener {
                profileViewModel.signOut()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (imgUri != null) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Use this picture as Profile Picture?")
                .setPositiveButton("Yes") { _, _ ->
                    profileViewModel.changeProfilePic(imgUri!!)
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
        }
    }

    private fun showProfilePic(liveUri: Uri? = null) {
        if (profileViewModel.profilePic != null) {
            Glide.with(requireActivity())
                .load(profileViewModel.profilePic)
                .circleCrop()
                .into(binding?.ivProfilePic!!)
        }

        if (liveUri != null) {
            Glide.with(requireActivity())
                .load(liveUri)
                .circleCrop()
                .into(binding?.ivProfilePic!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}