package com.bidblast.usecases.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.ActivityLoginBinding;
import com.bidblast.menus.mainmenu.MainMenuActivity;
import com.bidblast.repositories.ProcessErrorCodes;
import com.google.android.material.snackbar.Snackbar;

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
        setupLoginStatusListener();
    }

    private void setupLoginButtonClick() {
        binding.loginButton.setOnClickListener(v -> {
            if (validateFields()) {
                if(viewModel.getLoginRequestStatus().getValue() != RequestStatus.LOADING) {
                    String email = binding.emailEditText.getText().toString().trim();
                    String password = binding.passwordEditText.getText().toString().trim();

                    viewModel.login(email, password);
                }
            }
        });
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    private boolean validateFields() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

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

    private void setupLoginStatusListener() {
        viewModel.getLoginRequestStatus().observe(this, requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                startMainMenuActivity();
            }

            if (requestStatus == RequestStatus.ERROR) {
                ProcessErrorCodes errorCode = viewModel.getLoginErrorCode().getValue();

                if(errorCode != null) {
                    showLoginError(errorCode);
                }
            }
        });
    }

    private void showLoginError(ProcessErrorCodes errorCode) {
        String errorMessage = "";

        switch (errorCode) {
            case REQUEST_FORMAT_ERROR:
                errorMessage = getString(R.string.login_invalid_credentials_toast_message);
                break;
            case FATAL_ERROR:
                errorMessage = getString(R.string.login_error_toast_message);
                break;
            default:
                errorMessage = getString(R.string.login_error_toast_message);
        }

        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
    }
}