package com.capstone.cleanup.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.BuildConfig
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: MainRepository) : ViewModel() {
    val isSuccess = repository.isSuccess
    val errMsg = repository.errMsg

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) Log.d(TAG_LOAD_STATE, "Start Loading")
            _isLoading.value = true

            if (BuildConfig.DEBUG) Log.d(TAG_LOAD_STATE, "Loading")
            val isLoginProcessed = launch(Dispatchers.IO) { repository.loginWithEmailAndPassword(email, password) }.isCompleted

            if (isLoginProcessed) {
                if (BuildConfig.DEBUG) Log.d(TAG_LOAD_STATE, "Stop Loading")
                _isLoading.value = false
            }
        }
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

    companion object {
        private const val TAG_LOAD_STATE = "Loading State"
    }
}