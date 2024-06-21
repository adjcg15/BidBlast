package com.bidblast.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String avatar;
    private String email;
    private List<String> roles;

    public User() {}

    public User(int id, String fullName, String phoneNumber, String avatar, String email, List<String> roles) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.email = email;
        this.roles = roles;
    }

    public User(int id, String fullName, String email, String phoneNumber, String avatar) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.email = email;
    }

    protected User(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        phoneNumber = in.readString();
        avatar = in.readString();
        email = in.readString();
        roles = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fullName);
        dest.writeString(phoneNumber);
        dest.writeString(avatar);
        dest.writeString(email);
        dest.writeStringList(roles);
    }
}
