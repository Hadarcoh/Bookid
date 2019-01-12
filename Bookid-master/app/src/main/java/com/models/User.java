package com.models;

import com.google.firebase.database.Exclude;

public class User {
    private String phoneNum;
    private String uid;
    private String key;

    public User(){
        //empty cost needed
    }

    public User(String uid, String phoneNum){
        this.phoneNum = phoneNum;
        this.uid = uid;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String mPhoneNum) {
        this.phoneNum = mPhoneNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String mUid) {
        this.uid = mUid;
    }

    @Exclude
    public String getKey() { return key; }

    @Exclude
    public void setKey(String key) { this.key = key; }
}
