package com.bidblast.api.responses.authentication;

public class UserRegisterJSONResponse {
    private String message;
    private Account account;

    public static class Account {
        private int idAccount;
        private String email;
        private int idProfile;

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

        public int getIdProfile() {
            return idProfile;
        }

        public void setIdProfile(int idProfile) {
            this.idProfile = idProfile;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}


