package com.example.hives;

public class Comment {
    public String comment, date, time, username, uid,key;

    public Comment(){

    }

    public Comment(String comment, String date, String time, String username, String uid, String key) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
        this.uid = uid;
        this.key=key;
    }
    public String getKey() {
        return key;
    }
    public String getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
