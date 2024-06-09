package com.capstone.cleanup.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: MainRepository) : ViewModel() {
    val isSuccess = repository.isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loginWithEmailAndPassword(email: String, password: String) {
        repository.loginWithEmailAndPassword(email, password)
    }

    fun signIn() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.signIn()
            } finally {
                _isLoading.value = false
            }
        }
    }
}