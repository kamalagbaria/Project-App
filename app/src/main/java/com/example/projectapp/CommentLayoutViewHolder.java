package com.example.projectapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentLayoutViewHolder extends RecyclerView.ViewHolder {

    TextView ownerNameComment;
    TextView newCommentBar;
    TextView commentText;

    public CommentLayoutViewHolder(@NonNull View itemView) {
        super(itemView);
        this.ownerNameComment = itemView.findViewById(R.id.owner_name_comment);
        this.newCommentBar = itemView.findViewById(R.id.new_comment_bar);
        this.commentText = itemView.findViewById(R.id.comment_text);
    }
}
