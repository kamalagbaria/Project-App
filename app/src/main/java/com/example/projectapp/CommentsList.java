package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentsList extends AppCompatActivity {

    public FirebaseAuth mAuth;
    private ListView commentsLV;
    private Button addComment;

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
        String questionKey = getIntent().getStringExtra("question_key");
        DatabaseReference commentsDatabase = FirebaseDatabase.getInstance().getReference()
                .child("comments").child(questionKey);
        FirebaseListAdapter answerAdapter = new FirebaseListAdapter<Comment>(this, Comment.class,
                android.R.layout.simple_list_item_1, commentsDatabase) {
            @Override
            protected void populateView(View v, Comment model, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getText());
            }
        };

        commentsLV.setAdapter(answerAdapter);
    }

    public void addComment(View view){
        Intent i = new Intent(this, SubmitCommentActivity.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        startActivity(i);
    }
}