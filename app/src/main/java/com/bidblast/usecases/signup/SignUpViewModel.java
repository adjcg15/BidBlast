package com.bidblast.usecases.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.bidblast.R;
import com.bidblast.api.RequestStatus;
import com.bidblast.api.requests.authentication.UserRegisterBody;
import com.bidblast.lib.ImageToolkit;
import com.bidblast.lib.ValidationToolkit;
import com.bidblast.model.User;
import com.bidblast.repositories.AuthenticationRepository;
import com.bidblast.repositories.IEmptyProcessStatusListener;
import com.bidblast.repositories.ProcessErrorCodes;

public class SignUpViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isValidFullName = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidPassword = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidConfirmPassword = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> signUpRequestStatus = new MutableLiveData<>();
    private final MutableLiveData<ProcessErrorCodes> signUpErrorCode = new MutableLiveData<>();
    private final MutableLiveData<String> avatarBase64 = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidPasswordRules = new MutableLiveData<>();

    public LiveData<Boolean> isValidFullName() {
        return isValidFullName;
    }

    public LiveData<Boolean> isValidEmail() {
        return isValidEmail;
    }

    public LiveData<Boolean> isValidPassword() {
        return isValidPassword;
    }

    public LiveData<Boolean> isValidConfirmPassword() {
        return isValidConfirmPassword;
    }

    public LiveData<RequestStatus> getSignUpRequestStatus() {
        return signUpRequestStatus;
    }

    public LiveData<ProcessErrorCodes> getSignUpErrorCode() {
        return signUpErrorCode;
    }

    public LiveData<String> getAvatarBase64() {
        return avatarBase64;
    }

    public LiveData<Boolean> isValidPasswordRules() {
        return isValidPasswordRules;
    }

    public void setAvatarBase64(String base64Image) {
        avatarBase64.setValue(base64Image);
    }

    public void validatePasswordRules(String password) {
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[\\W_].*");
        boolean validLength = password.length() >= 8;

        isValidPasswordRules.setValue(hasUppercase && hasNumber && hasSpecialChar && validLength);
    }

    public void validateFullName(String fullName) {
        boolean validationResult = !fullName.isEmpty();
        isValidFullName.setValue(validationResult);
        Log.d("SignUpViewModel", "Full Name Valid: " + validationResult);
    }

    public void validateEmail(String email) {
        boolean validationResult = ValidationToolkit.isValidEmail(email);
        isValidEmail.setValue(validationResult);
        Log.d("SignUpViewModel", "Email Valid: " + validationResult);
    }

    public void validatePassword(String password) {
        boolean validationResult = ValidationToolkit.isValidUserPassword(password);
        isValidPassword.setValue(validationResult);
        Log.d("SignUpViewModel", "Password Valid: " + validationResult);
    }
    public void validateConfirmPassword(String password, String confirmPassword) {
        boolean validationResult = !password.isEmpty() && !confirmPassword.isEmpty() && password.equals(confirmPassword);
        isValidConfirmPassword.setValue(validationResult);
        Log.d("SignUpViewModel", "Confirm Password Valid: " + validationResult);
    }

    public void register(Context context, String fullName, String email, String phoneNumber, String password, String confirmPassword) {
        validateFullName(fullName);
        validateEmail(email);
        validatePassword(password);
        validateConfirmPassword(password, confirmPassword);

        if (Boolean.TRUE.equals(isValidFullName().getValue()) &&
                Boolean.TRUE.equals(isValidEmail().getValue()) &&
                Boolean.TRUE.equals(isValidPassword().getValue()) &&
                Boolean.TRUE.equals(isValidConfirmPassword().getValue())) {

            signUpRequestStatus.setValue(RequestStatus.LOADING);
            String avatarBase64 = getAvatarBase64().getValue();

            if (avatarBase64 == null || avatarBase64.isEmpty()) {
                avatarBase64 = null;
            }
            String phoneNumberToSend = phoneNumber.isEmpty() ? null : phoneNumber;

            UserRegisterBody registerBody = new UserRegisterBody(fullName, email, phoneNumberToSend, avatarBase64, password);
            new AuthenticationRepository().createUser(registerBody, new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Log.d("SignUpViewModel", "Account created successfully");
                    signUpRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ProcessErrorCodes errorStatus) {
                    Log.e("SignUpViewModel", "Error creating account: " + errorStatus);
                    signUpErrorCode.setValue(errorStatus);
                    signUpRequestStatus.setValue(RequestStatus.ERROR);
                }
            });
        } else {
            signUpRequestStatus.setValue(RequestStatus.ERROR);
        }
    }

    public void updateUser(Context context, User user, String password) {
        validateFullName(user.getFullName());
        validateEmail(user.getEmail());

        boolean isPasswordValid = true;
        if (password != null && !password.isEmpty()) {
            validatePassword(password);
            isPasswordValid = Boolean.TRUE.equals(isValidPassword().getValue());
        }

        if (Boolean.TRUE.equals(isValidFullName().getValue()) &&
                Boolean.TRUE.equals(isValidEmail().getValue()) &&
                isPasswordValid) {

            signUpRequestStatus.setValue(RequestStatus.LOADING);

            UserRegisterBody registerBody = new UserRegisterBody(user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getAvatar(), password);
            new AuthenticationRepository().updateUser(registerBody, new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    Log.d("SignUpViewModel", "Account updated successfully");
                    signUpRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ProcessErrorCodes errorStatus) {
                    Log.e("SignUpViewModel", "Error updating account: " + errorStatus);
                    signUpErrorCode.setValue(errorStatus);
                    signUpRequestStatus.setValue(RequestStatus.ERROR);
                }
            });
        } else {
            signUpRequestStatus.setValue(RequestStatus.ERROR);
        }
    }

}
