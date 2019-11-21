package com.example.hives;

public class HivesRetrieve {
    private String title;
    //private String HiveInfo;
    //private String HiveUserId;
    //private String HiveUserName;

    private String image;

    public HivesRetrieve() {
    }

    public HivesRetrieve(String hiveName,String hiveImage) {
        title = hiveName;
       // HiveInfo = hiveInfo;
        //HiveUserId = hiveUserId;
        //HiveUserName = hiveUserName;
        image =hiveImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String hiveName) {
        title = hiveName;
    }

   /* public String getHiveInfo() {
        return HiveInfo;
    }

    public void setHiveInfo(String hiveInfo) {
        HiveInfo = hiveInfo;
    }

    public String getHiveUserId() {
        return HiveUserId;
    }

    public void setHiveUserId(String hiveUserId) {
        HiveUserId = hiveUserId;
    }

    public String getHiveUserName() {
        return HiveUserName;
    }

    public void setHiveUserName(String hiveUserName) {
        HiveUserName = hiveUserName;
    }*/
    public String getImage() {
        return image;
    }

    public void setImage(String hiveImage) {
        image = hiveImage;
    }
}
