package com.example.mirarai.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mirarai.R
import com.example.mirarai.adapter.GetAdapter
import com.example.mirarai.databinding.ActivityFetchDataBinding
import com.example.mirarai.model.ApiResponse
import com.example.mirarai.viewmodel.MyViewModel

class FetchDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFetchDataBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var adapter: GetAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_fetch_data)

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        viewModel.apiData.observe(this, Observer {
            Log.d("ApiResponse", "onCreate: $it")
            initRecycler(it)

        })

        viewModel.fetchData()
        //viewModel.fetchUserData(1)


    }

    private fun initRecycler(list: List<ApiResponse>) {
        binding.rvUserData.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvUserData.setHasFixedSize(true)
        binding.rvUserData.itemAnimator = DefaultItemAnimator()
        adapter = GetAdapter(this, list)
        binding.rvUserData.adapter = adapter
    }

}