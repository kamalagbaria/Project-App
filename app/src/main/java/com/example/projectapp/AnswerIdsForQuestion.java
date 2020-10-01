package com.example.projectapp;

import java.util.ArrayList;

public class AnswerIdsForQuestion {
    ArrayList<Integer> answersIds = new ArrayList<>();
    int oldIndex;

    public AnswerIdsForQuestion(ArrayList<Integer> answersIds, int oldIndex) {
        this.answersIds = answersIds;
        this.oldIndex = oldIndex;
    }

    public AnswerIdsForQuestion() {
    }

    public ArrayList<Integer> getAnswerIds() {
        return answersIds;
    }

    public void setAnswerIds(ArrayList<Integer> answerIds) {
        this.answersIds = answerIds;
    }

    public int getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(int oldIndex) {
        this.oldIndex = oldIndex;
    }
}
