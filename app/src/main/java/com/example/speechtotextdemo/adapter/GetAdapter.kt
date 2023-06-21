package com.example.speechtotextdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speechtotextdemo.databinding.ListItemBinding
import com.example.speechtotextdemo.model.ApiResponse

class GetAdapter(private val context: Context, var list: List<ApiResponse>) :

    RecyclerView.Adapter<GetAdapter.ViewHolder>() {
    private lateinit var itemBinding: ListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        itemBinding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            itemBinding.tvData.text = list[position].body
        }
    }


    override fun getItemCount(): Int {
       return  list.size
    }


    class ViewHolder(itemView: ListItemBinding) : RecyclerView.ViewHolder(itemView.root) {

    }
}