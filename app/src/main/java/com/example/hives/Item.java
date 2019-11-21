package com.example.hives;




public class Item {

    private String title;
    private String image;

    public Item(String title, String image) {
        this.title = title;
        this.image = image;
    }

    public Item(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
