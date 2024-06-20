package com.capstone.cleanup.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.repository.MainRepository
import kotlinx.coroutines.launch

class ArticleViewModel(private val repository: MainRepository) : ViewModel() {
    val articles = repository.articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getArticles() {
        _isLoading.value = true
        try {
            viewModelScope.launch {
                repository.getArticles()
            }
        } finally {
            _isLoading.value = false
        }
    }


}