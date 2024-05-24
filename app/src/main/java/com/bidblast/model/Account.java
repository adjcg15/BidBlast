package com.bidblast.model;

public class Account {
    private int idAccount;
    private String email;

    public Account(int idAccount, String email) {
        this.idAccount = idAccount;
        this.email = email;
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
