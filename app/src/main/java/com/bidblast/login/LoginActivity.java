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
            if (validateFields()) {
                startMainMenuActivity();
            }
        });
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    private boolean validateFields() {
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();

        viewModel.validateEmail(email);
        viewModel.validatePassword(password);

        return Boolean.TRUE.equals(viewModel.isValidEmail().getValue())
                && Boolean.TRUE.equals(viewModel.isValidPassword().getValue());
    }

    private void setupFieldsValidations() {
        viewModel.isValidEmail().observe(this, isValidEmail -> {
            if (isValidEmail) {
                binding.emailErrorTextView.setVisibility(View.GONE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.emailErrorTextView.setVisibility(View.VISIBLE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidPassword().observe(this, isValidPassword -> {
            if (isValidPassword) {
                binding.passwordErrorTextView.setVisibility(View.GONE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.passwordErrorTextView.setVisibility(View.VISIBLE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
    }
}