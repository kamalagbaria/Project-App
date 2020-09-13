package com.example.projectapp;

import java.io.Serializable;

public class Comment implements Serializable {

    private String text;
    private String ownerId;

    public Comment(){

    }

    public Comment(String text, String uuid){
        this.text = text;
        this.ownerId = uuid;
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
