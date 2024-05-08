package com.bidblast.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.lib.ValidationToolkit;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isValidEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidPassword = new MutableLiveData<>();

    public LoginViewModel() {

    }

    public LiveData<Boolean> isValidEmail() {
        return isValidEmail;
    }

    public LiveData<Boolean> isValidPassword() {
        return isValidPassword;
    }

    public void validatePassword(String password) {
        boolean validationResult = ValidationToolkit.isValidUserPassword(password);

        isValidPassword.setValue(validationResult);
    }

    public void validateEmail(String email) {
        boolean validationResult = ValidationToolkit.isValidEmail(email);

        isValidEmail.setValue(validationResult);
    }
}
