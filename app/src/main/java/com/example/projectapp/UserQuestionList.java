package com.example.projectapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.Serializable;
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
                ((TextView)view.findViewById(android.R.id.text1)).setText(question.getTitle());
                ((TextView)view.findViewById(android.R.id.text2)).setText(question.getContent());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                        i.putExtra("question", (Serializable) Adapter.getItem(position));
                        i.putExtra("question_key", Adapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });

            }
        };
        questionList.setAdapter(Adapter);
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