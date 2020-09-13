package com.example.projectapp;


import java.io.Serializable;

public class Question  implements Serializable {

    private String title;
    private String content;
    private String ownerId;

    public Question(){

    }

    public Question(String title, String content, String ID){
        this.title = title;
        this.content = content;
        this.ownerId = ID;
    }

    public String getContent() {
        return content;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
