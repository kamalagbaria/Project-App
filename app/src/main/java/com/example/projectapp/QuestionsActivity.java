package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class QuestionsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreRecyclerOptions<Question> options;
    private FirestoreRecyclerAdapter<Question, QuestionViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        CollectionReference categories = db.collection("questions");
        Query categoryQuestions = categories.whereEqualTo("category", "physics");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewQuestions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        options = new FirestoreRecyclerOptions.Builder<Question>()
                .setQuery(categoryQuestions, Question.class).build();

        adapter = new FirestoreRecyclerAdapter<Question, QuestionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull Question model) {
                holder.questionTitle.setText(model.getTitle());
                holder.questionContent.setText(model.getContent());
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
                    ProgressBar bar = findViewById(R.id.progressBar);
                    bar.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
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