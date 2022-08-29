package com.example.sampleproject.Models;

public class Member {
    private String imageUri;

    public Member() {  }
    public Member(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
