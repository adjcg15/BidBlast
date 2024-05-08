package com.bidblast.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bidblast.R;
import com.bidblast.databinding.ActivityLoginBinding;
import com.bidblast.databinding.ActivityMainMenuBinding;
import com.bidblast.mainmenu.MainMenuActivity;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoginButtonClick();
    }

    private void setupLoginButtonClick() {
        binding.loginButton.setOnClickListener(v -> {
            startMainMenuActivity();
        });
    }

    private void startMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}