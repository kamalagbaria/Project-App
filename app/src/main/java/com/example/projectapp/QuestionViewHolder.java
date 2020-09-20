package com.example.projectapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionViewHolder extends RecyclerView.ViewHolder{

    TextView questionTitle;
    TextView questionContent;

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        this.questionTitle = itemView.findViewById(R.id.question_title);
        this.questionContent = itemView.findViewById(R.id.question_content);
    }


}

