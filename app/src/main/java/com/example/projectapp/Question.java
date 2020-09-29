package com.example.projectapp;


import java.io.Serializable;

public class Question  implements Serializable {

    private String title;
    private String content;
    private String ownerId;
    private String category;
    private float difficulty;
    private long questionUploadTime=System.currentTimeMillis();



    public Question(){ }

    public Question(String title, String content, String ID, String category){
        this.title = title;
        this.content = content;
        this.ownerId = ID;
        this.category=category;
        this.difficulty = 0;
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

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public long getQuestionUploadTime() { return questionUploadTime; }

    public String getCategory() {
        return category;
    }
}
