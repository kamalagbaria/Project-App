package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SubmitQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);

        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleET=  findViewById(R.id.titleET);
                EditText contentET =  findViewById(R.id.contentET);
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();

                final Question question = new Question(title, content,
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                FirebaseDatabase.getInstance().getReference().child("questions").push()
                        .setValue(question, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference reference) {
                                if (databaseError != null) {
                                    Toast.makeText(SubmitQuestionActivity.this,"Unable to submit question",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //start change
                                    // can't find out if it works
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().
                                            child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    mDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User user= dataSnapshot.getValue(User.class);
                                            assert user != null;
                                            ArrayList<Question> newList = user.AddQuestion(question);
                                            Log.w("TAG", "add question");
                                            FirebaseDatabase.getInstance().getReference().
                                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("myQuestions").setValue(newList);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    //end change
                                    startActivity(new Intent(SubmitQuestionActivity.this, MainActivity.class));
                                    Toast.makeText(SubmitQuestionActivity.this,"Question submitted",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
