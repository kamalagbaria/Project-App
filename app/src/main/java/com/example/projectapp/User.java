package com.example.projectapp;

import java.util.ArrayList;

public class User {

    private ArrayList<Question> myQuestions;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String id;
    private String location;
    private ArrayList<Question> lastViewed=new ArrayList<>();

    public ArrayList<Question> getLastViewed() {
        return lastViewed;
    }
    public User(String fullName, String firstName, String lastName, String email, String location, String id) {
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.location = location;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Question> AddQuestion(Question newQuestion){
        this.myQuestions.add(newQuestion);
        return myQuestions;
    }
    public ArrayList<Question> addLastViewed(Question question){
        this.lastViewed.add(question);
        return this.lastViewed;
    }

}
