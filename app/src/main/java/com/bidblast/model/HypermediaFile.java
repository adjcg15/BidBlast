package com.bidblast.model;

import java.util.Objects;

public class HypermediaFile {
    private int id;
    private String content;
    private String name;
    private String mimeType;

    public HypermediaFile() { }

    public HypermediaFile(int id, String content, String name, String mimeType) {
        this.id = id;
        this.content = content;
        this.name = name;
        this.mimeType = mimeType;
    }
    public HypermediaFile(String content, String name, String mimeType) {
        this.content = content;
        this.name = name;
        this.mimeType = mimeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HypermediaFile that = (HypermediaFile) o;
        return id == that.id && Objects.equals(content, that.content) && Objects.equals(name, that.name) && Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, name, mimeType);
    }
}
