package com.example.mirarai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.mirarai.databinding.ActivityPaginationBinding;

import java.util.ArrayList;

public class PaginationActivity extends AppCompatActivity {


    ActivityPaginationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pagination);
        initRecyclerView();
    }

    private void initRecyclerView() {
        ArrayList<model> list = new ArrayList<>();
        model model = new model();
        for (int i = 0; i < 50; i++) {
            model.setName("nitesh");
            list.add(model);
        }


        binding.rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rv.setHasFixedSize(true);
        binding.rv.setItemAnimator(new DefaultItemAnimator());
        Adapter adapter = new Adapter(this, list);
        binding.rv.setAdapter(adapter);

    }
}