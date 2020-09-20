package com.example.projectapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class QuestionDetailActivity extends AppCompatActivity {

    private Button answerbt;
    private RatingBar ratingBar;
    public FirebaseAuth mAuth;
    FirebaseListAdapter answerAdapter;
    private TextView difficulty;
    private ImageButton rateBtn;
    private float rateValue;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        final Question question = (Question) getIntent().getSerializableExtra("question");
        assert question != null;
        loadQuestion(question);
        mAuth = FirebaseAuth.getInstance();
        answerbt = findViewById(R.id.answerBtn);
        rateBtn = findViewById(R.id.imageButton);
        //difficulty =findViewById(R.id.DifficultyTextView);
        ratingBar = findViewById(R.id.rating_rating_bar);
        ratingBar.setRating(question.getDifficulty());
        if (mAuth.getCurrentUser() == null){
            answerbt.setVisibility(View.INVISIBLE);
         //   difficulty.setVisibility(View.INVISIBLE);
            rateBtn.setVisibility(View.INVISIBLE);
        }
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ratingBar.setFocusable(false);

        final String questionKey = getIntent().getStringExtra("question_key");
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateQuestion(question, questionKey);
            }
        });

        ListView listView = findViewById(R.id.answersLV);
        //get instance of database
        assert questionKey != null;
        Query answersDatabase = FirebaseDatabase.getInstance().getReference()
                .child("answers").child(questionKey);

        FirebaseListOptions<Answer> options = new FirebaseListOptions.Builder<Answer>()
                .setQuery(answersDatabase, Answer.class)
                .setLayout(android.R.layout.simple_list_item_1)
                .build();

         answerAdapter = new FirebaseListAdapter<Answer>(options) {
            @Override
            protected void populateView(View view, Answer answer, final int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(answer.getText());
            }
        };
        listView.setAdapter(answerAdapter);
    }

    private void loadQuestion(Question question){
        TextView questionTextView = findViewById(R.id.questionTV);
        questionTextView.setText(question.getTitle());

        TextView descriptionTextView = findViewById(R.id.descriptionTV);
        descriptionTextView.setText(question.getContent());
    }

    public void submitAnswer(View view){
        Intent i = new Intent(this, SubmitAnswerActivity.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        startActivity(i);
    }
    //Added for comments
    public void GoTOComments(View view){
        Intent i = new Intent(this, CommentsList.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        startActivity(i);
    }
    @SuppressLint("InflateParams")
    private void RateQuestion(final Question question, final String Key){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetailActivity.this);
        View layout;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        layout = inflater.inflate(R.layout.rating, null);
        final RatingBar rating_Bar = layout.findViewById(R.id.ratingBar);
        builder.setTitle("Rate Question");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                rateValue = rating_Bar.getRating();
                rateValue = (((question.getDifficulty() + rateValue) / 2) % 5);
                question.setDifficulty(rateValue);
                FirebaseDatabase.getInstance().getReference()
                        .child("questions").child(Key).child("difficulty").setValue(rateValue);
            }
        });
        builder.setNegativeButton("No,Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.setView(layout);
        builder.show();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        answerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        answerAdapter.stopListening();
    }
}
