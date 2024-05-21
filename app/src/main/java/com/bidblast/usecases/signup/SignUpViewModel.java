package com.bidblast.usecases.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.api.requests.authentication.UserRegisterBody;
import com.bidblast.lib.ValidationToolkit;
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

    public void validateFullName(String fullName) {
        boolean validationResult = !fullName.isEmpty();
        isValidFullName.setValue(validationResult);
    }

    public void validateEmail(String email) {
        boolean validationResult = ValidationToolkit.isValidEmail(email);
        isValidEmail.setValue(validationResult);
    }

    public void validatePassword(String password) {
        boolean validationResult = ValidationToolkit.isValidUserPassword(password);
        isValidPassword.setValue(validationResult);
    }

    public void validateConfirmPassword(String password, String confirmPassword) {
        boolean validationResult = password.equals(confirmPassword);
        isValidConfirmPassword.setValue(validationResult);
    }

    public void register(String fullName, String email, String phoneNumber, String avatar, String password, String confirmPassword) {
        validateFullName(fullName);
        validateEmail(email);
        validatePassword(password);
        validateConfirmPassword(password, confirmPassword);

        if (Boolean.TRUE.equals(isValidFullName.getValue()) &&
                Boolean.TRUE.equals(isValidEmail.getValue()) &&
                Boolean.TRUE.equals(isValidPassword.getValue()) &&
                Boolean.TRUE.equals(isValidConfirmPassword.getValue())) {

            signUpRequestStatus.setValue(RequestStatus.LOADING);

            UserRegisterBody registerBody = new UserRegisterBody(fullName, email, phoneNumber, avatar, password);
            new AuthenticationRepository().createAccount(registerBody, new IEmptyProcessStatusListener() {
                @Override
                public void onSuccess() {
                    signUpRequestStatus.setValue(RequestStatus.DONE);
                }

                @Override
                public void onError(ProcessErrorCodes errorStatus) {
                    signUpErrorCode.setValue(errorStatus);
                    signUpRequestStatus.setValue(RequestStatus.ERROR);
                }
            });
        } else {
            signUpRequestStatus.setValue(RequestStatus.ERROR);
        }
    }
}

