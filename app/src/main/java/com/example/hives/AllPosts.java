package com.example.hives;

public class AllPosts {
    public String date;
    public String description;
    public String hiveimage;
    private String hivename;
    public String postimage;
    public String time;
    public String username;

    public AllPosts() {
    }
    public AllPosts(String date, String description, String hiveimage, String hivename, String postimage, String time,String username) {
        this.date = date;
        this.description = description;
        this.hiveimage = hiveimage;
        this.hivename = hivename;
        this.postimage = postimage;
        this.time = time;
        this.username=username;
    }
    public String getDate() {
        return date; }
    public void setDate(String date) {
        this.date = date; }
    public String getDescription() {
        return description; }
    public void setDescription(String description) {
        this.description = description; }
    public String getHiveimage() {
        return hiveimage;
    }
    public void setHiveimage(String hiveimage) {
        this.hiveimage = hiveimage;
    }
    public String getHivename() {
        return hivename;
    }
    public void setHivename(String hivename) {
        this.hivename = hivename;
    }
    public String getPostimage() {
        return postimage;
    }
    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
