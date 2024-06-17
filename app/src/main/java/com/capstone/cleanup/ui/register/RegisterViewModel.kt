package com.capstone.cleanup.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {
    val isSuccess = repository.isSuccess
    val errMsg = repository.errMsg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val isRegisterProcessed = launch(Dispatchers.IO) { repository.register(email, password, name) }.isCompleted

            if (isRegisterProcessed) {
                _isLoading.value = false
            }
        }
    }
}