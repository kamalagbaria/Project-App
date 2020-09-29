package com.example.projectapp;

public class QuestionWrapper {

    public QuestionWrapper(Question question, String key) {
        this.question = question;
        this.key = key;
    }

    public QuestionWrapper() {
        this.question=new Question();
        this.key="";
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private Question question;
    private String key;
}
