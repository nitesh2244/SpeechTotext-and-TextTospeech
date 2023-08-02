package com.example.mirarai.auth.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.mirarai.R;
import com.example.mirarai.auth.model.LoginResponse;
import com.example.mirarai.auth.viewmodel.LoginViewModel;
import com.example.mirarai.databinding.ActivityLoginBinding;
import com.example.mirarai.util.ProgressDialog;
import com.example.mirarai.util.ValidationUtil;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    ActivityLoginBinding binding;

    LoginViewModel loginViewModel;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = new LoginViewModel();
        loginViewModel.init(this);
        progressDialog = new ProgressDialog(this);
        initClick();
        attachObserver();
    }

    private void initClick() {
        binding.btnLogin.setOnClickListener(view -> {
            if (isValidate()) {
                progressDialog.show();
                String name = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                loginViewModel.login(name, password);
            }
        });
        binding.signUp.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    public boolean isValidate() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.edtEmail.getText()).toString().trim())) {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError(getResources().getString(R.string.enter_email));
            return false;
        } else if (!ValidationUtil.emailValidation(binding.edtEmail.getText().toString().trim())) {
            binding.edtEmail.setError(getResources().getString(R.string.valid_email));
            return false;
        } else if (TextUtils.isEmpty(Objects.requireNonNull(binding.edtPassword.getText()).toString().trim())) {
            binding.edtPassword.requestFocus();
            binding.edtPassword.setError(getResources().getString(R.string.enter_password));
            return false;
        } else {
            return true;
        }
    }

    private void attachObserver() {

        loginViewModel.getIsFailed().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressDialog.hide();
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.observeLoginResponse().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                progressDialog.hide();
                Toast.makeText(LoginActivity.this, "Login successfully.", Toast.LENGTH_SHORT).show();

            }
        });
    }


}