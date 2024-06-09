package com.capstone.cleanup.data.di

import android.content.Context
import com.capstone.cleanup.data.repository.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        return MainRepository.getInstance(context)
    }
}