package com.example.hives;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResult {
    public String title;
    public String hiveinfo;
    public String image;


    public SearchResult() {

    }

    public SearchResult(String title, String hiveinfo,String image) {
        this.title = title;
        this.hiveinfo = hiveinfo;
        this.image= image;

    }

    public String getimage() {
        return image;
    }

    public void setimage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHiveinfo() {
        return hiveinfo;
    }

    public void setHiveinfo(String hiveinfo) {
        this.hiveinfo = hiveinfo;
    }
}
