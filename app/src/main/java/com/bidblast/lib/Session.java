package com.bidblast.lib;

import com.bidblast.model.User;

public class Session {
    private static Session instance;
    private String token;

    private User user;

    private Session() { }

    public Session getInstance() {
        if(instance == null) {
            instance = new Session();
        }

        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
