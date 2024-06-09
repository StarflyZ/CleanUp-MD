package com.capstone.cleanup.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.cleanup.data.Articles
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.ui.adpter.ArticleAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ArticleViewModel(repository: MainRepository) : ViewModel() {
    private val articlesRef = repository.articlesRef
    private val option = FirestoreRecyclerOptions.Builder<Articles>()
        .setQuery(articlesRef, Articles::class.java)
        .build()

    val articleAdapter = ArticleAdapter(option)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        startLoading()
    }

    private fun startLoading() {
        articlesRef.addSnapshotListener { value, _ ->
            _isLoading.value = value?.metadata?.hasPendingWrites() == true
        }
    }
}