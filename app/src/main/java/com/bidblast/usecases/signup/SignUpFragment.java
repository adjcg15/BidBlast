package com.bidblast.usecases.signup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.databinding.FragmentSignUpBinding;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.model.User;
import com.bidblast.repositories.ProcessErrorCodes;
import com.bidblast.usecases.login.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;
    private boolean isPasswordVisible = false;
    private boolean isEdition = false;
    private User userToEdit;
    private static final String ARG_USER = "user";
    private static final String ARG_IS_EDITION = "is_edition";

    public static SignUpFragment newInstance(User user, boolean isEdition) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putBoolean(ARG_IS_EDITION, isEdition);
        fragment.setArguments(args);
        return fragment;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(requireContext(), "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
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
                        UCrop uCrop = UCrop.of(selectedImageUri, Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image")));
                        uCrop.withOptions(options);
                        uCrop.start(requireContext(), this);
                    }
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                if (isValidImageSize(croppedUri)) {
                    String base64Image = convertImageToBase64(croppedUri);
                    binding.imageSelected.setImageURI(croppedUri);
                    viewModel.setAvatarBase64(base64Image);
                } else {
                    Toast.makeText(requireContext(), "La imagen seleccionada es demasiado grande", Toast.LENGTH_SHORT).show();
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
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
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
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        if (getArguments() != null) {
            isEdition = getArguments().getBoolean(ARG_IS_EDITION, false);
            userToEdit = getArguments().getParcelable(ARG_USER);
            loadUserInformationOnView();
        }

        setupHeaderView();
        setupSignUpButtonClick();
        setupFieldsValidations();
        setupSignUpStatusListener();
        setupSelectPhotoButtonClick();
        setupPasswordToggle();
        setupConfirmPasswordToggle();
        setupPasswordRules();
        setupGoBackListener();
    }

    private void setupHeaderView() {
        if (isEdition) {
            binding.headerDefault.setVisibility(View.GONE);
            binding.headerEdit.setVisibility(View.VISIBLE);
            binding.logoImageView.setVisibility(View.GONE);
        } else {
            binding.headerDefault.setVisibility(View.VISIBLE);
            binding.headerEdit.setVisibility(View.GONE);
        }
    }

    private void loadUserInformationOnView() {
        if (userToEdit != null) {

            binding.fullNameEditText.setText(userToEdit.getFullName());
            binding.emailEditText.setText(userToEdit.getEmail());
                binding.phoneNumberEditText.setText(userToEdit.getPhoneNumber());
            } 
            else {
                binding.phoneNumberEditText.setText("");
            }
            if (userToEdit.getAvatar() != null && !userToEdit.getAvatar().isEmpty()) {
                binding.imageSelected.setImageBitmap(ImageToolkit.parseBitmapFromBase64(userToEdit.getAvatar()));
                viewModel.setAvatarBase64(userToEdit.getAvatar());
            } else {
                binding.imageSelected.setImageResource(R.drawable.avatar_icon);
                viewModel.setAvatarBase64(null);
            Log.d("SignUpFragment", "User ID: " + userToEdit.getId());
            Log.d("SignUpFragment", "Full Name: " + userToEdit.getFullName());
        }
    }

    private void setupPasswordToggle() {
        ImageView passwordToggle = binding.passwordToggle;
        EditText passwordEditText = binding.passwordEditText;

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
        EditText passwordEditText = binding.passwordEditText;
        TextView passwordRules = binding.passwordRules;

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

        if (password.matches(".*[\\W_].*")) {
            rules = rules.replace("• Al menos un carácter especial", "✔ Al menos un carácter especial");
        } else {
            rules = rules.replace("✔ Al menos un carácter especial", "• Al menos un carácter especial");
        }

        if (password.length() >= 8) {
            rules = rules.replace("• Extensión mínima de 8 caracteres", "✔ Extensión mínima de 8 caracteres");
        } else {
            rules = rules.replace("✔ Extensión mínima de 8 caracteres", "• Extensión mínima de 8 caracteres");
        }

        passwordRules.setText(rules);
    }

    private void setupSelectPhotoButtonClick() {
        binding.selectPhotoButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    showPermissionExplanationDialog(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    openGallery();
                }
            } else {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    showPermissionExplanationDialog(Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    openGallery();
                }
            }
        });
    }

    private void showPermissionExplanationDialog(String permission) {
        new AlertDialog.Builder(requireContext())
                .setTitle("BidBlast quiere acceder a tu galería")
                .setMessage("BidBlast usa el acceso a tu galería para realizar acciones como poder seleccionar tu foto de perfil")
                .setPositiveButton("Permitir", (dialog, which) -> requestPermissionLauncher.launch(permission))
                .setNegativeButton("No permitir", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
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

                    if (isEdition) {
                        updateUser(fullName, email, phoneNumber, password, confirmPassword);
                    } else {
                        registerUser(fullName, email, phoneNumber, password, confirmPassword);
                    }
                }
            }
        });
    }

    private void registerUser(String fullName, String email, String phoneNumber, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            viewModel.register(requireContext(), fullName, email, phoneNumber, password, confirmPassword);
        } else {
            showPasswordMismatchError();
        }
    }
    private void updateUser(String fullName, String email, String phoneNumber, String password, String confirmPassword) {
        User updatedUser;
        String phoneNumberToSend = (phoneNumber == null || phoneNumber.isEmpty()) ? null : phoneNumber;

        if (!password.isEmpty() && password.equals(confirmPassword)) {
            updatedUser = new User(userToEdit.getId(), fullName, email, phoneNumberToSend, viewModel.getAvatarBase64().getValue());
            viewModel.updateUser(requireContext(), updatedUser, password);
        } else if (password.isEmpty() && confirmPassword.isEmpty()) {
            updatedUser = new User(userToEdit.getId(), fullName, email, phoneNumberToSend, viewModel.getAvatarBase64().getValue());
            viewModel.updateUser(requireContext(), updatedUser, null);
        } else {
            showPasswordMismatchError();
        }
    }
    private boolean validateFields() {
        String fullName = binding.fullNameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String phoneNumber = binding.phoneNumberEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        viewModel.validateFullName(fullName);
        viewModel.validateEmail(email);
        if (isEdition) {
            if (!password.isEmpty() || !confirmPassword.isEmpty()) {
                viewModel.validatePassword(password);
                viewModel.validateConfirmPassword(password, confirmPassword);
                viewModel.validatePasswordRules(password);
            }
        } else {
            viewModel.validatePassword(password);
            viewModel.validateConfirmPassword(password, confirmPassword);
            viewModel.validatePasswordRules(password);
        }

        boolean isPasswordValid = Boolean.TRUE.equals(viewModel.isValidPassword().getValue());
        boolean isPasswordRulesValid = Boolean.TRUE.equals(viewModel.isValidPasswordRules().getValue());
        boolean isConfirmPasswordValid = Boolean.TRUE.equals(viewModel.isValidConfirmPassword().getValue());

        if (!isPasswordValid && (!password.isEmpty() || !confirmPassword.isEmpty())) {
            binding.passwordError.setVisibility(View.VISIBLE);
            binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
        } else {
            binding.passwordError.setVisibility(View.GONE);
            binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_background);
            binding.passwordRulesError.setVisibility(View.GONE);
        }

        if (!isPasswordRulesValid && (!password.isEmpty() || !confirmPassword.isEmpty())) {
            binding.passwordRulesError.setVisibility(View.VISIBLE);
        } else {
            binding.passwordRulesError.setVisibility(View.GONE);
        }

        if (!isConfirmPasswordValid && !password.isEmpty() && !confirmPassword.isEmpty()) {
            binding.unverifiedPassword.setVisibility(View.VISIBLE);
            binding.confirmPasswordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
        } else {
            binding.unverifiedPassword.setVisibility(View.GONE);
            binding.confirmPasswordEditText.setBackgroundResource(R.drawable.basic_input_background);
        }

        return Boolean.TRUE.equals(viewModel.isValidFullName().getValue())
                && Boolean.TRUE.equals(viewModel.isValidEmail().getValue())
                && (isEdition ? true : isPasswordValid && isConfirmPasswordValid && isPasswordRulesValid);
    }

    private void setupFieldsValidations() {
        viewModel.isValidFullName().observe(getViewLifecycleOwner(), isValidFullName -> {
            if (isValidFullName) {
                binding.fullNameError.setVisibility(View.GONE);
                binding.fullNameEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.fullNameError.setVisibility(View.VISIBLE);
                binding.fullNameEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });
        viewModel.isValidEmail().observe(getViewLifecycleOwner(), isValidEmail -> {
            if (isValidEmail) {
                binding.emailError.setVisibility(View.GONE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.emailError.setVisibility(View.VISIBLE);
                binding.emailEditText.setBackgroundResource(R.drawable.basic_input_error_background);
            }
        });

        viewModel.isValidPassword().observe(getViewLifecycleOwner(), isValidPassword -> {
            if (isValidPassword) {
                binding.passwordError.setVisibility(View.GONE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_background);
            } else {
                binding.passwordError.setVisibility(View.VISIBLE);
                binding.passwordEditText.setBackgroundResource(R.drawable.basic_input_error_background);
                binding.passwordRulesError.setVisibility(View.VISIBLE);
            }
        });

        viewModel.isValidConfirmPassword().observe(getViewLifecycleOwner(), isValidConfirmPassword -> {
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
        viewModel.getSignUpRequestStatus().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus != null) {
                switch (requestStatus) {
                    case LOADING:
                        showLoading();
                        break;
                    case DONE:
                        hideLoading();
                        showConfirmationDialog(isEdition);
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
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showPasswordMismatchError() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.signup_password_mismatch_title)
                .setMessage(R.string.signup_password_mismatch_error)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showConfirmationDialog(boolean isUpdate) {
        String title = isUpdate ? "Actualización exitosa" : "Registro exitoso";
        String message = isUpdate ? "Tu información ha sido actualizada exitosamente. Inicia sesión nuevamente" : "Tu cuenta ha sido creada exitosamente.";

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> navigateToLogin())
                .setCancelable(false)
                .show();
    }


    private void setupConfirmPasswordToggle() {
        ImageView confirmPasswordToggle = binding.confirmPasswordToggle;
        EditText confirmPasswordEditText = binding.confirmPasswordEditText;

        confirmPasswordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPasswordToggle.setImageResource(R.drawable.eye_icon);
            } else {
                confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPasswordToggle.setImageResource(R.drawable.eye_icon);
            }
            isPasswordVisible = !isPasswordVisible;
            confirmPasswordEditText.setSelection(confirmPasswordEditText.length());
        });
    }

    private void setupGoBackListener() {
        ImageView goBackImageView = binding.goBackImageView;
        goBackImageView.setOnClickListener(v -> navigateToLogin());
    }
}
