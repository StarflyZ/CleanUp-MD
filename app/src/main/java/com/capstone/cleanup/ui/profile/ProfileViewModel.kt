package com.capstone.cleanup.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: MainRepository) : ViewModel() {
    private val currentUser = repository.currentUser
    val username = currentUser?.displayName
    val profilePic = currentUser?.photoUrl

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}