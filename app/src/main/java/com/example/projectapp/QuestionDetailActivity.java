package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionDetailActivity extends AppCompatActivity {

    private Button answerbt;
    private RatingBar ratingBar;
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Question question = (Question) getIntent().getSerializableExtra("question");
        assert question != null;
        loadQuestion(question);
        //start change
        mAuth = FirebaseAuth.getInstance();
        answerbt = findViewById(R.id.answerBtn);
        ratingBar = findViewById(R.id.rating_rating_bar);
        if (mAuth.getCurrentUser() == null){
            answerbt.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
        }
        int userRating = (int) ratingBar.getRating();
        //end changed
        ListView listView = findViewById(R.id.answersLV);

        String questionKey = getIntent().getStringExtra("question_key");

        //get instance of database
        DatabaseReference answersDatabase = FirebaseDatabase.getInstance().getReference()
                .child("answers").child(questionKey);

        FirebaseListAdapter answerAdapter = new FirebaseListAdapter<Answer>(this, Answer.class,
                android.R.layout.simple_list_item_1, answersDatabase) {
            @Override
            protected void populateView(View view, Answer answer, final int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(answer.getText());
            }
        };

        listView.setAdapter(answerAdapter);
    }

    private void loadQuestion(Question question){
        TextView questionTextView = (TextView) findViewById(R.id.questionTV);
        questionTextView.setText(question.getTitle());

        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTV);
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
}
