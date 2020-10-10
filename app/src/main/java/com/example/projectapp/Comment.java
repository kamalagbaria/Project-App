package com.example.projectapp;

import java.io.Serializable;
import java.util.UUID;

public class Comment implements Serializable {

    private String text;
    private String ownerId;
    private String ownerName;
    private String commentId;

    public Comment(){

    }

    public Comment(String text, String uid, String ownerName){
        this.text = text;
        this.ownerId = uid;
        this.ownerName = ownerName;
        this.commentId = UUID.randomUUID().toString();;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getText(){
        return text;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setOwnerId(String uuid){
        this.ownerId = uuid;
    }
}
