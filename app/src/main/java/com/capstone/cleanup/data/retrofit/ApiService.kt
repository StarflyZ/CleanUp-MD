package com.capstone.cleanup.data.retrofit

import com.capstone.cleanup.data.response.ArticlesResponse
import retrofit2.http.GET

interface ApiService {
    @GET("article")
    suspend fun getArticles(): ArticlesResponse
}