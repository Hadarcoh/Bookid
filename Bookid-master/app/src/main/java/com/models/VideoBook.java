package com.models;

import com.google.firebase.database.Exclude;

public class VideoBook {
    private String key;
    private String pic;
    private String video;

    public VideoBook(){

    }

    public VideoBook(String pic, String video) {
        this.pic = pic;
        this.video = video;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
