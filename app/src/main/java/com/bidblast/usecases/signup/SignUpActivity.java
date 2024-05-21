package com.bidblast.usecases.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.ActivitySignUpBinding;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setupSignUpButtonClick();
        setupFieldsValidations();
        setupSignUpStatusListener();
    }

    private void setupSignUpButtonClick() {
        binding.signUpButton.setOnClickListener(v -> {
            if (validateFields()) {
                if (viewModel.getSignUpRequestStatus().getValue() != RequestStatus.LOADING) {
                    String fullName = binding.fullNameEditText.getText().toString().trim();
                    String email = binding.emailEditText.getText().toString().trim();
                    String phoneNumber = binding.phoneNumberEditText.getText().toString().trim();
                    String password = binding.passwordEditText.getText().toString().trim();
                    String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
                    String avatar = "";

                    if (password.equals(confirmPassword)) {
                        viewModel.register(fullName, email, phoneNumber, avatar, password, confirmPassword);
                    } else {
                        showPasswordMismatchError();
                    }
                }
            }
        });
    }

    private boolean validateFields() {
        String fullName = binding.fullNameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        viewModel.validateFullName(fullName);
        viewModel.validateEmail(email);
        viewModel.validatePassword(password);
        viewModel.validateConfirmPassword(password, confirmPassword);

        return Boolean.TRUE.equals(viewModel.isValidFullName().getValue())
                && Boolean.TRUE.equals(viewModel.isValidEmail().getValue())
                && Boolean.TRUE.equals(viewModel.isValidPassword().getValue())
                && Boolean.TRUE.equals(viewModel.isValidConfirmPassword().getValue());
    }

    private void setupFieldsValidations() {
        viewModel.isValidFullName().observe(this, isValidFullName -> {
            if (isValidFullName) {
                binding.fullNameError.setVisibility(View.GONE);
                binding.fullNameEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.fullNameError.setVisibility(View.VISIBLE);
                binding.fullNameEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidEmail().observe(this, isValidEmail -> {
            if (isValidEmail) {
                binding.emailError.setVisibility(View.GONE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.emailError.setVisibility(View.VISIBLE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidPassword().observe(this, isValidPassword -> {
            if (isValidPassword) {
                binding.passwordError.setVisibility(View.GONE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.passwordError.setVisibility(View.VISIBLE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidConfirmPassword().observe(this, isValidConfirmPassword -> {
            if (isValidConfirmPassword) {
                binding.unverifiedPassword.setVisibility(View.GONE);
                binding.confirmPasswordEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.unverifiedPassword.setVisibility(View.VISIBLE);
                binding.confirmPasswordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
    }

    private void setupSignUpStatusListener() {
        viewModel.getSignUpRequestStatus().observe(this, requestStatus -> {
            if (requestStatus == RequestStatus.DONE) {
                Snackbar.make(binding.getRoot(), R.string.signup_success_message, Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            } else if (requestStatus == RequestStatus.ERROR) {
                ProcessErrorCodes errorCode = viewModel.getSignUpErrorCode().getValue();
                if (errorCode != null) {
                    showSignUpError(errorCode);
                }
            }
        });
    }

    private void showSignUpError(ProcessErrorCodes errorCode) {
        String errorMessage = "";
        switch (errorCode) {
            case REQUEST_FORMAT_ERROR:
                errorMessage = getString(R.string.signup_invalid_input_toast_message);
                break;
            case FATAL_ERROR:
                errorMessage = getString(R.string.signup_error_toast_message);
                break;
            default:
                errorMessage = getString(R.string.signup_error_toast_message);
        }
        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void showPasswordMismatchError() {
        Snackbar.make(binding.getRoot(), getString(R.string.signup_password_mismatch_error), Snackbar.LENGTH_SHORT).show();
    }
}
