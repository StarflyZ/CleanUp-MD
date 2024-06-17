package com.capstone.cleanup.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MainRepository) : ViewModel() {
    private val currentUser = repository.currentUser
    val username = currentUser?.displayName
    val profilePic = currentUser?.photoUrl

    val profilePicLive = repository.liveProfilePic

    val isUploading = repository.isUploading

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

    fun changeProfilePic(imageUri: Uri) {
        repository.changeProfilePic(imageUri)
    }
}