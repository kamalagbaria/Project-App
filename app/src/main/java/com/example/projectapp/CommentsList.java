package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class CommentsList extends AppCompatActivity {

    public FirebaseAuth mAuth;
    private ListView commentsLV;
    private Button addComment;
    FirebaseListAdapter commentsAdapter;

    private String questionKey;
    private String commentId;

    private FirebaseRecyclerOptions<Comment> options; //new
    private FirebaseRecyclerAdapter<Comment, CommentLayoutViewHolder> adapter; //new
    private RecyclerView recyclerView; //new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_list);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        //commentsLV = findViewById(R.id.commentsLV);
        this.recyclerView = findViewById(R.id.commentsRecyclerView);
        addComment = findViewById(R.id.addbutton);
        if(mAuth.getCurrentUser() == null){
            addComment.setVisibility(View.INVISIBLE);
        }

        this.questionKey = getIntent().getStringExtra("question_key");
        this.commentId = getIntent().getStringExtra("comment_id");
        Toast.makeText(this, this.commentId, Toast.LENGTH_LONG).show();

        final Query commentsDatabase = FirebaseDatabase.getInstance().getReference()
                .child("comments").child(questionKey);

//        FirebaseListOptions<Comment> options = new FirebaseListOptions.Builder<Comment>()
//                .setQuery(commentsDatabase, Comment.class)
//                .setLayout(R.layout.comment_layout)
//                .build();

        options = new FirebaseRecyclerOptions.Builder<Comment>() //new
                .setQuery(commentsDatabase, Comment.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Comment, CommentLayoutViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentLayoutViewHolder holder, int position, @NonNull Comment comment)
            {
                holder.ownerNameComment.setText(comment.getOwnerName());
                holder.commentText.setText(comment.getText());

                holder.newCommentBar.setVisibility(View.GONE);

                if(commentId != null && Objects.equals(comment.getCommentId(), commentId))
                {
                    holder.newCommentBar.setVisibility(View.VISIBLE);
                    final Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.blink);

                    // set animation listener
                    holder.newCommentBar.setAnimation(animBlink);
                    animBlink.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.newCommentBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }

            @NonNull
            @Override
            public CommentLayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,
                        parent, false);
                return new CommentLayoutViewHolder(view);
            }

            @NonNull
            @Override
            public Comment getItem(int position) {
                return super.getItem(getItemCount() - position - 1);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (commentId != null) {
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (adapter.getItem(i).getCommentId().equals(commentId)) {
                            scrollToComment(i);
                            break;
                        }
                    }
                }
            }
        };

//         commentsAdapter = new FirebaseListAdapter<Comment>(options) {
//            @Override
//            protected void populateView(final View v, Comment model, int position)
//            {
//                ((TextView)v.findViewById(R.id.owner_name_comment)).setText(model.getOwnerName());
//                ((TextView)v.findViewById(R.id.comment_text)).setText(model.getText());
//                if(commentId != null && Objects.equals(getItem(position).getCommentId(), commentId))
//                {
//                    v.findViewById(R.id.new_comment_bar).setVisibility(View.VISIBLE);
//                    final Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
//                            R.anim.blink);
//
//                    // set animation listener
//                    v.findViewById(R.id.new_comment_bar).setAnimation(animBlink);
//                    animBlink.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            v.findViewById(R.id.new_comment_bar).setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                }
//            }
//
//             @NonNull
//             @Override
//             public Comment getItem(int position) {
//                 return super.getItem(getCount() - position - 1);
//             }
//
//             @Override
//             public void onDataChanged() {
//                 super.onDataChanged();
//                 int x = 0;
//                 if (commentId != null) {
//                     for (int i = 0; i < getCount(); i++) {
//                         if (getItem(i).getCommentId().equals(commentId)) {
//                             scrollToComment(getCount() - i - 1);
//                             break;
//                         }
//                     }
//                 }
//             }
//         };

//         if(this.commentId == null) {
//             commentsLV.setAdapter(commentsAdapter);
//         }

        if (this.commentId == null) {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            recyclerView.setLayoutManager(mLayoutManager);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    mLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            recyclerView.setAdapter(adapter);
        }
    }

    private void scrollToComment(int position)
    {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLayoutManager.scrollToPositionWithOffset(position, 0);
        recyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);
    }

    public void addComment(View view){
        Intent i = new Intent(this, SubmitCommentActivity.class);
        i.putExtra("question_key", getIntent().getStringExtra("question_key"));
        i.putExtra("question_owner_id", getIntent().getStringExtra("question_owner_id"));
        startActivity(i);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        if(this.commentId != null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            super.onBackPressed();
            startActivity(intent);
        }
        else
        {
            super.onBackPressed();
        }
        return true;

    }
}