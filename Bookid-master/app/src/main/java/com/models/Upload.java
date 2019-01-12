package com.models;

import com.google.firebase.database.Exclude;

public class Upload {
    private String name;
    private String imageUrl;
    private String key;
    private boolean check;

    public Upload(){
        //empty cost needed
    }

    public Upload(String name, String imageUrl){
        if(name.trim().equals("")){
            name = "No Name";
        }

        this.name = name;
        this.imageUrl = imageUrl;
        this.check = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.imageUrl = mImageUrl;
    }

    @Exclude
    public String getKey() { return key; }

    @Exclude
    public void setKey(String key) { this.key = key; }

    public boolean isCheck() { return check; }

    public void setCheck(boolean check) { this.check = check; }
}
