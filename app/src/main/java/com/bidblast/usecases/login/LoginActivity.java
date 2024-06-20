package com.bidblast.usecases.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.ActivityLoginBinding;
import com.bidblast.lib.Session;
import com.bidblast.menus.mainmenu.MainMenuActivity;
import com.bidblast.menus.moderatormenu.ModeratorMenuActivity;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.signup.SignUpActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        boolean showSessionFinishedToast = intent.getBooleanExtra("showSessionFinishedToast", false);
        if (showSessionFinishedToast) {
            Snackbar.make(binding.getRoot(), getString(R.string.login_session_finished_toast_message), Snackbar.LENGTH_SHORT).show();
        }

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
        String MODERATOR_ROLE = "MODERATOR";
        List<String> userRoles = Session.getInstance().getUser().getRoles();

        Intent intent;
        if(userRoles.contains(MODERATOR_ROLE)) {
            intent = new Intent(this, ModeratorMenuActivity.class);
        } else {
            intent = new Intent(this, MainMenuActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        this.finish();
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

    public void openSignUpActivity(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}