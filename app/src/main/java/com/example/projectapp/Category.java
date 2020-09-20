package com.example.projectapp;

public class Category {
    private String name;
    private String imageName;

    public Category(String name, String imageName) {
        this.name = name;
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Category() {
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}
