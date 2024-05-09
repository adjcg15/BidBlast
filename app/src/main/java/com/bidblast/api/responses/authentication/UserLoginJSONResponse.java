package com.bidblast.api.responses.authentication;

import java.util.List;

public class UserLoginJSONResponse {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String avatar;
    private String email;
    private List<String> roles;
    private String token;

    public UserLoginJSONResponse() { }

    public UserLoginJSONResponse(int id, String fullName, String phoneNumber, String avatar, String email, List<String> roles, String token) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.email = email;
        this.roles = roles;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
