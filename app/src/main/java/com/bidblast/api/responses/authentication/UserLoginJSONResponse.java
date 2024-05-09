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

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getToken() {
        return token;
    }
}
