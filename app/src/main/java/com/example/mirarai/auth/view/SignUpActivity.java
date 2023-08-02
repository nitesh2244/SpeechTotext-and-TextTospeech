package com.example.mirarai.auth.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.mirarai.R;
import com.example.mirarai.auth.model.LoginResponse;
import com.example.mirarai.auth.model.SignUpResponse;
import com.example.mirarai.auth.viewmodel.LoginViewModel;
import com.example.mirarai.databinding.ActivitySignUpBinding;
import com.example.mirarai.util.ProgressDialog;
import com.example.mirarai.util.Session;
import com.example.mirarai.util.ValidationUtil;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    LoginViewModel loginViewModel;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        loginViewModel = new LoginViewModel();
        loginViewModel.init(this);
        progressDialog = new ProgressDialog(this);
        initClick();
        attachObserver();
    }

    private void initClick() {

        binding.btnSignUp.setOnClickListener(view -> {
            if (isValidate()) {
                progressDialog.show();
                String name = binding.edtName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                loginViewModel.signUp(name, email, password);
            }
        });

        binding.signIn.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void attachObserver() {

        loginViewModel.getIsFailed().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressDialog.hide();
                Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.observeSignUpResponse().observe(this, new Observer<SignUpResponse>() {
            @Override
            public void onChanged(SignUpResponse signUpResponse) {
                progressDialog.hide();
                Toast.makeText(SignUpActivity.this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                //Session.getInstance(SignUpActivity.this).putString("token", signUpResponse.getToken());
            }
        });
    }

    public boolean isValidate() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.edtName.getText()).toString().trim())) {
            binding.edtName.requestFocus();
            binding.edtName.setError(getResources().getString(R.string.enter_name));
            return false;

        } else if (TextUtils.isEmpty(Objects.requireNonNull(binding.edtEmail.getText()).toString().trim())) {
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

        } else if (!(binding.edtPassword.getText().toString().length() >= 6)) {
            binding.edtPassword.setError(getResources().getString(R.string.password_length));
            return false;

        } else if (TextUtils.isEmpty(Objects.requireNonNull(binding.edtConfirmPassword.getText()).toString().trim())) {
            binding.edtConfirmPassword.requestFocus();
            binding.edtConfirmPassword.setError(getResources().getString(R.string.enter_password));
            return false;

        } else if (!(binding.edtPassword.getText().toString().equals(binding.edtConfirmPassword.getText().toString()))) {
            binding.edtConfirmPassword.setError(getResources().getString(R.string.password_not_match));
            return false;

        } else {
            return true;
        }
    }
}