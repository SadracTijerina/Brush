package com.example.brush;

public class Posts {

    private String date, description, postType, profilePicture, time, title, uid,
            username, price, postimage;

    public Posts() {

    }

    public Posts(String date, String description, String postType, String profilePicture,
                 String time, String title, String uid, String username, String price,
                 String postimage) {

        this.date = date;
        this.description = description;
        this.postType = postType;
        this.profilePicture = profilePicture;
        this.time = time;
        this.title = title;
        this.uid = uid;
        this.username = username;
        this.price = price;
        this.postimage = postimage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }
}