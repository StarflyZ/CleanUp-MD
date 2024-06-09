package com.capstone.cleanup.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.capstone.cleanup.databinding.FragmentProfileBinding
import com.capstone.cleanup.ui.ViewModelFactory
import com.capstone.cleanup.ui.home.HomeActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireActivity())
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

        binding?.apply {
            tvUsername.text = profileViewModel.username

            if (profileViewModel.profilePic != null) {
                Glide.with(requireContext())
                    .load(profileViewModel.profilePic)
                    .circleCrop()
                    .into(ivProfilePic)
            }

            btnLogout.setOnClickListener {
                profileViewModel.signOut()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}