package com.example.projectapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;


public class QuestionDetailActivity extends AppCompatActivity {

    private Button answerbt;
    private RatingBar ratingBar;
    public FirebaseAuth mAuth;
    FirebaseListAdapter answerAdapter;
    private ImageButton rateBtn;
    private float rateValue;
    private String questionKey;
    private String answerId;

    private FirebaseUser firebaseUser;
    private String ownerOfQuestionId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListView listView;
    private Question question;

    private FirebaseRecyclerOptions<Answer> options; //new
    private FirebaseRecyclerAdapter<Answer, AnswerLayoutViewHolder> adapter; //new
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        question = (Question) getIntent().getSerializableExtra("question");
        assert question != null;
        loadQuestion(question);
        mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = mAuth.getCurrentUser();
        answerbt = findViewById(R.id.answerBtn);
        rateBtn = findViewById(R.id.imageButton);
        ratingBar = findViewById(R.id.rating_rating_bar);

        ratingBar.setRating(question.getDifficulty());
        if (this.firebaseUser == null) {
            answerbt.setVisibility(View.INVISIBLE);
            rateBtn.setVisibility(View.INVISIBLE);
        }
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ratingBar.setFocusable(false);

        questionKey = getIntent().getStringExtra("question_key");
        this.answerId = getIntent().getStringExtra("answer_id");

        this.getOwnerOfQuestionId();

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateQuestion(question, questionKey);
            }
        });

        this.recyclerView = findViewById(R.id.answersRecyclerView); //new


        //listView = findViewById(R.id.answersLV);
        //get instance of database
        assert questionKey != null;
        Query answersDatabase = FirebaseDatabase.getInstance().getReference()
                .child("answers").child(questionKey);

        options = new FirebaseRecyclerOptions.Builder<Answer>() //new
                .setQuery(answersDatabase, Answer.class)
                .build();


        adapter = new FirebaseRecyclerAdapter<Answer, AnswerLayoutViewHolder>(options) { //new
            @Override
            protected void onBindViewHolder(@NonNull final AnswerLayoutViewHolder holder, final int position, @NonNull Answer answer) {
                holder.ownerName.setText(answer.getOwnerName());
                holder.answerText.setText(answer.getText());
                holder.newAnswerBar.setVisibility(View.GONE);

                if (answer.getText().equals("")) {
                    holder.answerText.setVisibility(View.GONE);
                }

                if (answer.getAnswerId().equals(answerId)) {
                    holder.newAnswerBar.setVisibility(View.VISIBLE);
                    final Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.blink);

                    // set animation listener
                    holder.newAnswerBar.setAnimation(animBlink);
                    animBlink.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.newAnswerBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

                if (answer.getImageUrl() != null && !answer.getImageUrl().equals("None")) {
                    holder.progressBarAnswerImage.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    storageRef.child("images/Answers/" + answer.getImageUrl()).getDownloadUrl().
                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(Uri.parse(uri.toString())).resize(1200, 1600)
                                            .onlyScaleDown().into(holder.answerImage);
                                    holder.progressBarAnswerImage.setVisibility(View.GONE);
                                    holder.answerImage.setVisibility(View.VISIBLE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            holder.progressBarAnswerImage.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @NonNull
            @Override
            public AnswerLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_layout,
                        parent, false);
                return new AnswerLayoutViewHolder(view);
            }

            @NonNull
            @Override
            public Answer getItem(int position) {
                return super.getItem(getItemCount() - position - 1);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (answerId != null) {
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (adapter.getItem(i).getAnswerId().equals(answerId)) {
                            scrollToAnswer(i);
                            break;
                        }
                    }
                }
            }
        };


        if (this.answerId == null) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            recyclerView.setLayoutManager(mLayoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            recyclerView.setAdapter(adapter);
        }


//        answerAdapter = new FirebaseListAdapter<Answer>(options) {
//            @Override
//            protected void populateView(@NonNull View view, @NonNull Answer answer, final int position) {
//
//                ((TextView)view.findViewById(R.id.owner_name)).setText(answer.getOwnerName());
//                ((TextView)view.findViewById(R.id.answerText)).setText(answer.getText());
//                if(answer.getText().equals("")){
//                    ((TextView)view.findViewById(R.id.answerText)).setVisibility(View.GONE);}
//                final ImageView imageView=view.findViewById(R.id.answerImage);
//                if (answer.getImageUrl()!=null && !answer.getImageUrl().equals("None")){
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    StorageReference storageRef = storage.getReference();
//                    storageRef.child("images/Answers/"+answer.getImageUrl()).getDownloadUrl().
//                            addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Picasso.get().load(Uri.parse(uri.toString())).resize(1200, 1600)
//                                    .onlyScaleDown().into(imageView);
//                            imageView.setVisibility(View.VISIBLE);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//            }
//
//        };

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                user.addLastViewed(questionKey);
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("lastViewed").setValue(user.getLastViewed());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }

    }

    private void scrollToAnswer(int position) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPositionWithOffset(position, 0);
        recyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);
    }


    private void getOwnerOfQuestionId() {
        FirebaseDatabase.getInstance().getReference().child("questions").child(questionKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ownerOfQuestionId = snapshot.child("ownerId").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadQuestion(final Question question) {
        TextView questionTextView = findViewById(R.id.questionTV);
        questionTextView.setText(question.getTitle());

        TextView descriptionTextView = findViewById(R.id.descriptionTV);
        descriptionTextView.setText(question.getContent());


    }

    public void submitAnswer(View view) {
        Intent i = new Intent(this, SubmitAnswerActivity.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        i.putExtra("question_owner_id", ownerOfQuestionId);
        i.putExtra("question_title", question.getTitle());
        startActivity(i);
    }

    //Added for comments
    public void GoTOComments(View view) {
        Intent i = new Intent(this, CommentsList.class);
        i.putExtra("question_owner_id", ownerOfQuestionId);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        startActivity(i);
    }

    @SuppressLint("InflateParams")
    private void RateQuestion(final Question question, final String Key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionDetailActivity.this);
        View layout;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        layout = inflater.inflate(R.layout.rating, null);
        final RatingBar rating_Bar = layout.findViewById(R.id.ratingBar);
        builder.setTitle("Rate Question");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                rateValue = rating_Bar.getRating();
                rateValue = (((question.getDifficulty() + rateValue) / 2) % 5);
                question.setDifficulty(rateValue);
                ratingBar.setRating(question.getDifficulty());
                FirebaseDatabase.getInstance().getReference()
                        .child("questions").child(Key).child("difficulty").setValue(rateValue);
                setRateQuestionToFirestore(Key);
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

    private void setRateQuestionToFirestore(String questionId) {
        db.collection("users").document(this.firebaseUser.getUid()).collection("questions")
                .document(questionId).update("difficulty", rateValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //answerAdapter.startListening();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //answerAdapter.stopListening();
        adapter.startListening();
    }

}
