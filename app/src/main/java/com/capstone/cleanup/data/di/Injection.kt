package com.capstone.cleanup.data.di

import android.content.Context
import com.capstone.cleanup.data.repository.MainRepository
import com.capstone.cleanup.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val apiService = ApiConfig.getApiService()
        return MainRepository.getInstance(context, apiService)
    }
}