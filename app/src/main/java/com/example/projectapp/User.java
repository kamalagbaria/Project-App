package com.example.projectapp;

import java.util.ArrayList;

public class User {

    private String UserId;
    private String UserEmail;
    private String UserLocation;
    private String UserName;
    private ArrayList<Question> myQuestions;

    public User(String userId, String userEmail, String name) {
        UserId = userId;
        UserEmail = userEmail;
        UserName = name;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserLocation() {
        return UserLocation;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserLocation(String userLocation) {
        UserLocation = userLocation;
    }

    public ArrayList<Question> AddQuestion(Question newQuestion){
        this.myQuestions.add(newQuestion);
        return myQuestions;
    }

}
