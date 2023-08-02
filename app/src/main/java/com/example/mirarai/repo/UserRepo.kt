package com.example.mirarai.repo

import com.example.mirarai.network.RetrofitClient.apiService

class UserRepo {

    suspend fun getData(a: Int) = apiService.getApiDataByID(a)
}