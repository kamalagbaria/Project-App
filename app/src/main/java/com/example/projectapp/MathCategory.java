package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class MathCategory extends AppCompatActivity {

    private Query DatabaseRef;
    private FirebaseListAdapter Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_category);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button askMath=findViewById(R.id.askMath);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            askMath.setVisibility(View.INVISIBLE);
        }

        askMath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SubmitQuestionActivity.class);
                i.putExtra("Category","Mathematics");
                startActivity(i);
            }
        });

        final ListView listView = findViewById(R.id.rvContacts);

        //get instance of database
        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("questions");

        FirebaseListOptions<Question> options = new FirebaseListOptions.Builder<Question>()
                .setQuery(DatabaseRef.orderByChild("category").equalTo("Mathematics"), Question.class)
                .setLayout(android.R.layout.two_line_list_item)
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
        listView.setAdapter(Adapter);
        DatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProgressBar bar=findViewById(R.id.progressBar);
                ConstraintLayout main=findViewById(R.id.mainContent);
                bar.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Adapter.startListening();
    }

<<<<<<< HEAD

=======
>>>>>>> 9d80c84c045343f47244a8a9565717a2f3c130c4
    @Override
    protected void onStop() {
        super.onStop();
        Adapter.stopListening();
    }
}
