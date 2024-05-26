package com.bidblast.usecases.signup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.ActivitySignUpBinding;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;
    private static final int PERMISSION_REQUEST_READ_MEDIA_IMAGES = 100;
    private boolean isPasswordVisible = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Log.d("GalleryLauncher", "URI de la imagen seleccionada: " + selectedImageUri.toString());
                        UCrop.Options options = new UCrop.Options();
                        UCrop uCrop = UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), "cropped_image")));
                        uCrop.withOptions(options);
                        uCrop.start(this);
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                if (isValidImageSize(croppedUri)) {
                    String base64Image = convertImageToBase64(croppedUri);
                    binding.imageSelected.setImageURI(croppedUri);
                    viewModel.setAvatarBase64(base64Image);
                } else {
                    Toast.makeText(this, "La imagen seleccionada es demasiado grande", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UCrop", "Error al recortar la imagen: " + cropError.getMessage());
            }
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                return Base64.encodeToString(buffer, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isValidImageSize(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                int fileSize = inputStream.available();
                double fileSizeInMB = fileSize / (1024 * 1024.0);
                double maxSizeInMB = 0.5;
                return fileSizeInMB <= maxSizeInMB;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setupSignUpButtonClick();
        setupFieldsValidations();
        setupSignUpStatusListener();
        setupSelectPhotoButtonClick();
        setupPasswordToggle();
        setupPasswordRules();
    }
    private void setupPasswordToggle() {
        ImageView passwordToggle = findViewById(R.id.passwordToggle);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.eye_icon);
            } else {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordToggle.setImageResource(R.drawable.eye_icon);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.length());
        });
    }
    private void setupPasswordRules() {
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView passwordRules = findViewById(R.id.passwordRules);

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passwordRules.setVisibility(View.VISIBLE);
            } else {
                passwordRules.setVisibility(View.GONE);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.validatePasswordRules(s.toString());
                updatePasswordRules(s.toString(), passwordRules);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void updatePasswordRules(String password, TextView passwordRules) {
        String rules = getString(R.string.signup_password_rules);
        if (password.matches(".*[A-Z].*")) {
            rules = rules.replace("• Al menos una letra mayúscula", "✔ Al menos una letra mayúscula");
        } else {
            rules = rules.replace("✔ Al menos una letra mayúscula", "• Al menos una letra mayúscula");
        }
        if (password.matches(".*\\d.*")) {
            rules = rules.replace("• Al menos un número", "✔ Al menos un número");
        } else {
            rules = rules.replace("✔ Al menos un número", "• Al menos un número");
        }
        if (password.length() >= 10 && password.length() <= 15) {
            rules = rules.replace("• Extensión entre 10 y 15 caracteres", "✔ Extensión entre 10 y 15 caracteres");
        } else {
            rules = rules.replace("✔ Extensión entre 10 y 15 caracteres", "• Extensión entre 10 y 15 caracteres");
        }
        passwordRules.setText(rules);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSelectPhotoButtonClick() {
        binding.selectPhotoButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    showPermissionExplanationDialog(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    openGallery();
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    showPermissionExplanationDialog(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    openGallery();
                }
            }
        });
    }

    private void showPermissionExplanationDialog(String permission) {
        new AlertDialog.Builder(this)
                .setTitle("BidBlast quiere acceder a tu galería")
                .setMessage("BidBlast usa el acceso a tu galería para realizar acciones como poder seleccionar tu foto de perfil")
                .setPositiveButton("Permitir", (dialog, which) -> requestPermissionLauncher.launch(permission))
                .setNegativeButton("No permitir", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        galleryLauncher.launch(intent);
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

                    if (password.equals(confirmPassword)) {
                        viewModel.register(this, fullName, email, phoneNumber, password, confirmPassword);
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
        viewModel.validatePasswordRules(password);

        boolean isPasswordValid = Boolean.TRUE.equals(viewModel.isValidPassword().getValue());
        boolean isPasswordRulesValid = Boolean.TRUE.equals(viewModel.isValidPasswordRules().getValue());

        if (!isPasswordValid) {
            binding.passwordError.setVisibility(View.VISIBLE);
            binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
        } else {
            binding.passwordError.setVisibility(View.GONE);
            binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_background);
            binding.passwordRulesError.setVisibility(View.GONE);
        }

        if (!isPasswordRulesValid) {
            binding.passwordRulesError.setVisibility(View.VISIBLE);
        } else {
            binding.passwordRulesError.setVisibility(View.GONE);
        }

        return Boolean.TRUE.equals(viewModel.isValidFullName().getValue())
                && Boolean.TRUE.equals(viewModel.isValidEmail().getValue())
                && isPasswordValid
                && Boolean.TRUE.equals(viewModel.isValidConfirmPassword().getValue())
                && isPasswordRulesValid;
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
                binding.passwordRulesError.setVisibility(View.VISIBLE);
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
            if (requestStatus != null) {
                switch (requestStatus) {
                    case LOADING:
                        showLoading();
                        break;
                    case DONE:
                        hideLoading();
                        showConfirmationDialog();
                        break;
                    case ERROR:
                        hideLoading();
                        ProcessErrorCodes errorCode = viewModel.getSignUpErrorCode().getValue();
                        if (errorCode != null) {
                            showSignUpError(errorCode);
                        }
                        break;
                    default:
                        hideLoading();
                        break;
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

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showError() {
        Snackbar.make(binding.getRoot(), "Hubo un error en el registro", Snackbar.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showPasswordMismatchError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.signup_password_mismatch_title)
                .setMessage(R.string.signup_password_mismatch_error)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Registro exitoso")
                .setMessage("Tu cuenta ha sido creada exitosamente, inicia sesión para comenzar en el mundo de las subastas.")
                .setPositiveButton("OK", (dialog, which) -> navigateToLogin())
                .setCancelable(false)
                .show();
    }
}


