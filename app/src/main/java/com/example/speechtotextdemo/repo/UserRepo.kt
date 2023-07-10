package com.example.speechtotextdemo.repo

import com.example.speechtotextdemo.network.ApiService
import com.example.speechtotextdemo.network.RetrofitClient.apiService

class UserRepo {

    suspend fun getData(a: Int) = apiService.getApiDataByID(a)
}