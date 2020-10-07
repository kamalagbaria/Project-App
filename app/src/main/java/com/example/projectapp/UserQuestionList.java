package com.example.projectapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class UserQuestionList extends AppCompatActivity {

    private ListView questionList;
    private Query DatabaseRef;
    private FirebaseListAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_question_list);

        questionList = findViewById(R.id.userQuestions);

        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("questions");

        FirebaseListOptions<Question> options = new FirebaseListOptions.Builder<Question>()
                .setQuery(DatabaseRef.orderByChild("ownerId").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance()
                        .getCurrentUser()).getUid()), Question.class)
                .setLayout(R.layout.two_line_list_item)
                .build();
        Adapter = new FirebaseListAdapter<Question>(options) {
            @Override
            protected void populateView(View view, Question question, final int position) {
                ((TextView)view.findViewById(R.id.text1)).setText(question.getTitle());
                ((TextView)view.findViewById(R.id.text2)).setText(question.getContent());

                final String key = Adapter.getRef(position).getKey();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                        i.putExtra("question", (Serializable) Adapter.getItem(position));
                        i.putExtra("question_key", key);
                        startActivity(i);
                    }
                });
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(UserQuestionList.this)
                                .setTitle("Delete Question")
                                .setMessage("Are you sure you want to delete this question?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                        Query Query = ref.child("questions").
                                                child(Objects.requireNonNull(key));
                                        Query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                                    Snapshot.getRef().removeValue();
                                                    DeleteComments(ref,key);
                                                    DeleteAnswers(ref,key);
                                                    deleteQuestionFormLastViewed(key);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return true;
                    }
                });

            }
        };
        questionList.setAdapter(Adapter);
    }
    private void deleteQuestionFormLastViewed(final String key){
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                ArrayList<String> questionKeys = user.getLastViewed();
                                ArrayList<String> updatedQuestionWrappers = new ArrayList<>();
                                for (String questionKey : questionKeys) {
                                    if (!questionKey.equals(key)) {
                                        updatedQuestionWrappers.add(questionKey);
                                    }
                                }
                                mDatabase.child("lastViewed").setValue(updatedQuestionWrappers).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(UserQuestionList.this,
                                                        "Deleted question", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void DeleteComments(DatabaseReference reference, final String key){
        Query Query = reference.child("comments").
                child(Objects.requireNonNull(key));
        Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    Snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void DeleteAnswers(DatabaseReference reference, final String key){
        Query Query = reference.child("answers").
                child(Objects.requireNonNull(key));
        Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    Snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    protected void onStart() {
        super.onStart();
        Adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Adapter.stopListening();
    }
}