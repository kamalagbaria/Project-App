package com.example.projectapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_comment);
    }

    public void submitComment(View view){
        EditText commentET= findViewById(R.id.commentET);
        String text = commentET.getText().toString();

        Comment comment = new Comment(text,
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        String questionKey = getIntent().getStringExtra("question_key");

        FirebaseDatabase.getInstance().getReference().child("comments").child(questionKey).push()
                .setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Toast.makeText(SubmitCommentActivity.this,"Unable to submit comment",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SubmitCommentActivity.this,"comment submitted",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });


    }
}