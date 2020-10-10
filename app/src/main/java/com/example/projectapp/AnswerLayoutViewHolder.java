package com.example.projectapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswerLayoutViewHolder extends RecyclerView.ViewHolder {

    TextView ownerName;
    TextView questionState;
    TextView newAnswerBar;
    TextView answerText;
    ImageView answerImage;
    ProgressBar progressBarAnswerImage;

    public AnswerLayoutViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ownerName = itemView.findViewById(R.id.owner_name);
        this.questionState = itemView.findViewById(R.id. questionState);
        this.newAnswerBar = itemView.findViewById(R.id.new_answer_bar);
        this.answerText = itemView.findViewById(R.id.answerText);
        this.answerImage = itemView.findViewById(R.id.answerImage);
        this.progressBarAnswerImage = itemView.findViewById(R.id.progress_bar_answer_image);

    }
}
