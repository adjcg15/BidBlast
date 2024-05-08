package com.bidblast.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bidblast.R;
import com.bidblast.databinding.ActivityLoginBinding;
import com.bidblast.databinding.ActivityMainMenuBinding;
import com.bidblast.mainmenu.MainMenuActivity;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupLoginButtonClick();
        setupFieldsValidations();
    }

    private void setupLoginButtonClick() {
        binding.loginButton.setOnClickListener(v -> {
            startMainMenuActivity();
        });
    }

    private void setupFieldsValidations() {
        viewModel.isValidEmail().observe(this, isValidEmail -> {
            if (isValidEmail) {
                binding.emailErrorTextView.setVisibility(View.GONE);
                binding.emailErrorTextView.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.emailErrorTextView.setVisibility(View.VISIBLE);
                binding.emailErrorTextView.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidPassword().observe(this, isValidPassword -> {
            if (isValidPassword) {
                binding.passwordErrorTextView.setVisibility(View.GONE);
                binding.passwordErrorTextView.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.passwordErrorTextView.setVisibility(View.VISIBLE);
                binding.passwordErrorTextView.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}