package com.example.projectapp;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    LinearLayout buttonCategory;
    ImageView imageViewCategory;
    TextView textViewCategory;
    public CategoryViewHolder(@NonNull View itemView)
    {
        super(itemView);
        buttonCategory = itemView.findViewById(R.id.button_single_category);
        imageViewCategory = itemView.findViewById(R.id.image_category);
        textViewCategory = itemView.findViewById(R.id.category_name);

    }
}
