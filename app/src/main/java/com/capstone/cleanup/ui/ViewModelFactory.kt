package com.capstone.cleanup.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.cleanup.data.di.Injection.provideRepository
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.ui.article.ArticleViewModel
import com.capstone.cleanup.ui.login.LoginViewModel
import com.capstone.cleanup.ui.main.MainViewModel
import com.capstone.cleanup.ui.profile.ProfileViewModel
import com.capstone.cleanup.ui.register.RegisterViewModel

class ViewModelFactory(
    private val repository: MainRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ArticleViewModel::class.java) -> {
                ArticleViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun getInstance(context: Context) = ViewModelFactory(provideRepository(context))
    }
}