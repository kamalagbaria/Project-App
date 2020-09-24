package com.example.projectapp;

import android.net.Uri;

public class ItemAnswer {


    private String content;
    private Uri photoAnswer;

    public ItemAnswer(String content, Uri uri){
        this.content = content;
        this.photoAnswer = uri;
    }

    public String getContent() {
        return content;
    }

    public Uri getPhotoAnswer() {
        return photoAnswer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPhotoAnswer(Uri photoAnswer) {
        this.photoAnswer = photoAnswer;
    }
}
