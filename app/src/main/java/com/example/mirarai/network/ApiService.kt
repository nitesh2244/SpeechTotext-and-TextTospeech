package com.example.mirarai.network

import com.example.mirarai.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("comments")
    suspend fun getApiDataByID(@Query("postId") postId: Int): Response<List<ApiResponse>>
}