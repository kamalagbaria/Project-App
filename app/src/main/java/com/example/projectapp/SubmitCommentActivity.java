package com.example.projectapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class SubmitCommentActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_comment);
    }

    public void submitComment(View view){
        EditText commentET= findViewById(R.id.commentET);
        String text = commentET.getText().toString();

        final Comment comment = new Comment(text,
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        final String questionKey = getIntent().getStringExtra("question_key");

        FirebaseDatabase.getInstance().getReference().child("comments").child(questionKey).push()
                .setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Toast.makeText(SubmitCommentActivity.this,"Unable to submit comment",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SubmitCommentActivity.this,"Comment submitted",
                                    Toast.LENGTH_SHORT).show();
                            addCommentToFirestore(reference.getKey(), questionKey, comment);
                            finish();
                        }
                    }
                });
    }

    private void addCommentToFirestore(final String commentId, final String questionId, Comment comment)
    {
        String questionUserId = getIntent().getStringExtra("question_owner_id");
        assert questionUserId != null;
        db.collection("users").document(questionUserId).collection("questions")
                .document(questionId).collection("comments").document(commentId).set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                //good
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                //raise exception
            }
        });
        // finish();

    }
}