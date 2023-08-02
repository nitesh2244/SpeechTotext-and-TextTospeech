package com.example.mirarai.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mirarai.model.ApiResponse
import com.example.mirarai.model.ErrorResponse
import com.example.mirarai.network.RetrofitClient.apiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyViewModel : ViewModel() {

    private val _apiData = MutableLiveData<List<ApiResponse>>()
    val apiData: LiveData<List<ApiResponse>> = _apiData


    fun fetchData() {

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val postId = 1
                val response = apiService.getApiDataByID(postId)

                if (response.isSuccessful) {
                    _apiData.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        val errorCode = errorResponse.errorCode
                        val errorMessage = errorResponse.errorMessage
                        Log.d("ApiError", "Error code: $errorCode, Error message: $errorMessage")
                    } else {
                        Log.d("ApiError", "Unknown error occurred")
                    }
                }

            } catch (e: Exception) {
                // Handle error
                Log.d("ApiError", "fetchData: ${e.message}")
            }
        }
    }

    /*  fun fetchUserData(a: Int) {
          viewModelScope.launch {
              _apiData.value = userRepo.getData(a).body()
          }

      }*/


}