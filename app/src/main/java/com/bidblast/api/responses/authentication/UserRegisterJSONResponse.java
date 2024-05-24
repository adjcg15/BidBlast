package com.bidblast.api.responses.authentication;

import com.bidblast.model.Account;

public class UserRegisterJSONResponse {
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public UserRegisterJSONResponse(Account account) {
        this.account = account;
    }
}


