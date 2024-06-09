package com.capstone.cleanup.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.cleanup.data.repository.MainRepository

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {
    val isSuccess = repository.isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(email: String, password: String, name: String) {
        _isLoading.value = true
        try {
            repository.register(email, password, name)
        } finally {
            _isLoading.value = false
        }
    }
}