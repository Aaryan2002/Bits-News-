package com.example.bits_news;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.Map;

public class Posts implements Serializable {
    public String description, imageUrl, owner;
    public int dislikes, likes;
    public String uploadedOn;

    public Posts() { }

//    public Posts(String description, String imageUrl, String owner, int dislikes, int likes, FieldValue uploadedOn) {
//        this.description = description;
//        this.imageUrl = imageUrl;
//        this.owner = owner;
//        this.dislikes = dislikes;
//        this.likes = likes;
//        this.uploadedOn = uploadedOn;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(String uploadedOn) {
        this.uploadedOn = uploadedOn;
    }
}
