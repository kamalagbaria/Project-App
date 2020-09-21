package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.Objects;

public class GeneralCategory extends AppCompatActivity {

    String category;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerOptions<Question> options;
    private FirestoreRecyclerAdapter<Question, QuestionViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_category);

        category= Objects.requireNonNull(getIntent().getExtras()).getString("Category");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button askQuestion=findViewById(R.id.askQuestion);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            askQuestion.setVisibility(View.INVISIBLE);
        }

        askQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SubmitQuestionActivity.class);
                i.putExtra("Category",category);
                startActivity(i);
            }
        });

        CollectionReference categories = db.collection("questions");
        Query categoryQuestions = categories.whereEqualTo("category", category);

        RecyclerView recyclerView=findViewById(R.id.QuestionsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(categoryQuestions, Question.class).build();
        adapter = new FirestoreRecyclerAdapter<Question, QuestionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position, @NonNull Question model)
            {
                holder.questionTitle.setText(model.getTitle());
                holder.questionContent.setText(model.getContent());

                holder.questionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                        i.putExtra("question", adapter.getItem(position)); //Document itself
                        i.putExtra("question_key", (Serializable) adapter.getSnapshots().getSnapshot(position).getId()); // Document ID
                        startActivity(i);
                    }
                });
            }
            @NonNull
            @Override
            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_question_view_holder,
                        parent, false);
                return new QuestionViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        categoryQuestions.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    ProgressBar bar=findViewById(R.id.progressBar);
                    ConstraintLayout main=findViewById(R.id.mainContent);
                    bar.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Add askQuestion Button

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}