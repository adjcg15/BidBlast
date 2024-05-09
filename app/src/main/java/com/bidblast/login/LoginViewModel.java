package com.bidblast.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bidblast.api.RequestStatus;
import com.bidblast.lib.ValidationToolkit;
import com.bidblast.model.models.User;
import com.bidblast.model.repositories.AuthenticationRepository;
import com.bidblast.model.repositories.IProcessStatusListener;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isValidEmail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isValidPassword = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> loginRequestStatus = new MutableLiveData<>();

    public LoginViewModel() {

    }

    public LiveData<Boolean> isValidEmail() {
        return isValidEmail;
    }

    public LiveData<Boolean> isValidPassword() {
        return isValidPassword;
    }

    public LiveData<RequestStatus> getLoginRequestStatus() { return loginRequestStatus; }

    public void validatePassword(String password) {
        boolean validationResult = ValidationToolkit.isValidUserPassword(password);

        isValidPassword.setValue(validationResult);
    }

    public void validateEmail(String email) {
        boolean validationResult = ValidationToolkit.isValidEmail(email);

        isValidEmail.setValue(validationResult);
    }

    public void login(String email, String password) {
        loginRequestStatus.setValue(RequestStatus.LOADING);

        new AuthenticationRepository().login(
                email, password,
                new IProcessStatusListener<User>() {
                    @Override
                    public void onSuccess(User data) {
                        //TODO: save data in pojo
                        loginRequestStatus.setValue(RequestStatus.DONE);
                    }

                    @Override
                    public void onError() {
                        loginRequestStatus.setValue(RequestStatus.ERROR);
                    }
                }
        );
    }
}
