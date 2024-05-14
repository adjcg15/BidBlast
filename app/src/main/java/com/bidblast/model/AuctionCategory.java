package com.bidblast.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AuctionCategory implements Parcelable {
    private int id;

    private String title;

    private String description;
    private String keywords;

    public AuctionCategory() {}

    public AuctionCategory(int id, String title, String description, String keywords) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    protected AuctionCategory(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        keywords = in.readString();
    }

    public static final Creator<AuctionCategory> CREATOR = new Creator<AuctionCategory>() {
        @Override
        public AuctionCategory createFromParcel(Parcel in) {
            return new AuctionCategory(in);
        }

        @Override
        public AuctionCategory[] newArray(int size) {
            return new AuctionCategory[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuctionCategory that = (AuctionCategory) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, keywords);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(keywords);
    }
}
