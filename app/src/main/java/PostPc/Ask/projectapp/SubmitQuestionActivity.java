package PostPc.Ask.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.Objects;

public class SubmitQuestionActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);

        this.userId = (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getUid();
        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleET=  findViewById(R.id.titleET);
                EditText contentET =  findViewById(R.id.contentET);
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();

                final String category= Objects.requireNonNull(getIntent().getExtras()).getString("Category");

                final Question question = new Question(title, content,
                        userId,category);
                FirebaseDatabase.getInstance().getReference().child("questions").push()
                        .setValue(question, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference reference) {
                                if (databaseError != null) {
                                    Toast.makeText(SubmitQuestionActivity.this,"Unable to submit question",
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    //Add question to Firestore
                                    addQuestionToFirestore(reference.getKey(), question);
                                    Intent intent=new Intent(SubmitQuestionActivity.this,GeneralCategory.class);
                                    intent.putExtra("Category",category);
                                    Toast.makeText(SubmitQuestionActivity.this,"Question submitted",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void addQuestionToFirestore(String questionId, final Question question)
    {
        //AnswerIdsForQuestion answerId = new AnswerIdsForQuestion(new ArrayList<Integer>(), 0);
        db.collection("users").document(userId).collection("questions").document(questionId).set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }

//    private void addQuestionId(String questionId)
//    {
//        AnswerIdsForQuestion answerId = new AnswerIdsForQuestion(new ArrayList<Integer>(), 0);
//        db.collection("users").document(userId).collection("questions").document(questionId).set(answerId).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid)
//            {
//                //good
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e)
//            {
//                //raise exception
//            }
//        });
//    }
}
