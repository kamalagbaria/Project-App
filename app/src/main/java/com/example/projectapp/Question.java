package com.example.projectapp;


import java.io.Serializable;

public class Question  implements Serializable {

    private String title;
    private String content;
    private String ownerId;
    private String category;

    public String getCategory() {
        return category;
    }

    public Question(){

    }

    public Question(String title, String content, String ID,String category){
        this.title = title;
        this.content = content;
        this.ownerId = ID;
        this.category=category;
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
