package com.bidblast.api.requests.authentication;

public class UserRegisterBody {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String password;

    public UserRegisterBody(String fullName, String email, String phoneNumber, String avatar, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
