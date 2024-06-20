package com.capstone.cleanup.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.cleanup.data.Reports
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.ui.adpter.ReportAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val articles = repository.articles

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

    val currentUser = repository.currentUser

    private val reportRef = repository.reportRef.orderBy("timeStamp", Query.Direction.DESCENDING)
    private val reportOption = FirestoreRecyclerOptions.Builder<Reports>()
        .setQuery(reportRef, Reports::class.java)
        .build()

    val reportAdapter = ReportAdapter(reportOption)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
}