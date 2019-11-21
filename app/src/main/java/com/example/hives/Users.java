package com.example.hives;

public class Users {


    String bio;
    String username;

    public Users() {
    }

    public Users(String bio, String username) {
        this.bio = bio;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
