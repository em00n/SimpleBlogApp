package com.emon.simpleblogapp;

public class Post {
    private String title, discrip, uid, url;

    public Post() {
    }

    public Post(String title, String discrip, String uid, String url) {
        this.title = title;
        this.discrip = discrip;
        this.uid = uid;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDiscrip() {
        return discrip;
    }

    public void setDiscrip(String discrip) {
        this.discrip = discrip;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
