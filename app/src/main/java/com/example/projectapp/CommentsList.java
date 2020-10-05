package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CommentsList extends AppCompatActivity {

    public FirebaseAuth mAuth;
    private ListView commentsLV;
    private Button addComment;
    FirebaseListAdapter commentsAdapter;

    private String questionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);

        mAuth = FirebaseAuth.getInstance();
        commentsLV = findViewById(R.id.commentsLV);
        addComment = findViewById(R.id.addbutton);
        if(mAuth.getCurrentUser() == null){
            addComment.setVisibility(View.INVISIBLE);
        }

        this.questionKey = getIntent().getStringExtra("question_key");
        Query commentsDatabase = FirebaseDatabase.getInstance().getReference()
                .child("comments").child(questionKey);

        FirebaseListOptions<Comment> options = new FirebaseListOptions.Builder<Comment>()
                .setQuery(commentsDatabase, Comment.class)
                .setLayout(android.R.layout.simple_list_item_1)
                .build();

         commentsAdapter = new FirebaseListAdapter<Comment>(options) {
            @Override
            protected void populateView(View v, Comment model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getText());
            }
        };

        commentsLV.setAdapter(commentsAdapter);
    }

    public void addComment(View view){
        Intent i = new Intent(this, SubmitCommentActivity.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        i.putExtra("question_owner_id", getIntent().getStringExtra("question_owner_id"));
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentsAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        commentsAdapter.stopListening();
    }
}