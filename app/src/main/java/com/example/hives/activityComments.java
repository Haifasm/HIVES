package com.example.hives;

public class activityComments {

    public String uid,creatorid,postkey,username,profileimage,comment,time,date;

    public activityComments(){

    }

    public activityComments(String uid, String creatorid, String postkey, String username, String profileimage, String comment,String time, String date) {
        this.uid = uid;
        this.creatorid = creatorid;
        this.postkey = postkey;
        this.username = username;
        this.profileimage = profileimage;
        this.comment = comment;
        this.date=date;
        this.time=time;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
