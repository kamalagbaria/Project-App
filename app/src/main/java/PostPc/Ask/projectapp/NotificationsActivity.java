package PostPc.Ask.projectapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {

    private final String NOTIFICATIONS = "Notifications";
    private final String users = "users";
    private SwitchCompat newAnswerSwitch;
    private SwitchCompat newCommentSwitch;
    private SwitchCompat newRatingSwitch;
    private ProgressBar progressBarNotifications;
    private LinearLayout notificationsLayout;

    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Objects.requireNonNull(getSupportActionBar()).setTitle(NOTIFICATIONS);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        this.db = FirebaseFirestore.getInstance();
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        this.notificationsLayout = findViewById(R.id.notifications_layout);
        this.progressBarNotifications = findViewById(R.id.progress_bar_notifications);

        this.newAnswerSwitch = findViewById(R.id.new_answer_notify);
        this.newCommentSwitch = findViewById(R.id.new_comment_notify);
        this.newRatingSwitch = findViewById(R.id.new_rating_notify);

        this.loadSwitches();

        this.answerSwitchChanged();
        this.commentSwitchChanged();
        this.ratingSwitchChanged();

    }

    private void loadSwitches()
    {
        this.progressBarNotifications.setVisibility(View.VISIBLE);
        this.notificationsLayout.setVisibility(View.GONE);

        String notifications = "notifications";
        String questionNotifications = "question_notifications";
        db.collection(users).document(firebaseUser.getUid()).collection(notifications)
                .document(questionNotifications).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if(document.exists())
                    {
                        Boolean newAnswer = (Boolean) document.get("newAnswer");
                        assert newAnswer != null;
                        newAnswerSwitch.setChecked(newAnswer);

                        Boolean newComment = (Boolean) document.get("newComment");
                        assert newComment != null;
                        newCommentSwitch.setChecked(newComment);

                        Boolean newRating = (Boolean) document.get("newRating");
                        assert newRating != null;
                        newRatingSwitch.setChecked(newRating) ;

                        notificationsLayout.setVisibility(View.VISIBLE);
                        progressBarNotifications.setVisibility(View.GONE);

                    }
                    else
                    {
                        //Document doesn't exist
                    }
                }
                else
                {
                    //Todo Handle Error
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void answerSwitchChanged()
    {

        this.newAnswerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                progressBarNotifications.setVisibility(View.VISIBLE);
                notificationsLayout.setVisibility(View.GONE);

                String notifications = "notifications";
                String questionNotifications = "question_notifications";
                db.collection(users).document(firebaseUser.getUid()).collection(notifications)
                        .document(questionNotifications).update("newAnswer", b)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                notificationsLayout.setVisibility(View.VISIBLE);
                                progressBarNotifications.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                });
            }
        });

    }

    private void commentSwitchChanged()
    {
        this.newCommentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                progressBarNotifications.setVisibility(View.VISIBLE);
                notificationsLayout.setVisibility(View.GONE);

                String notifications = "notifications";
                String questionNotifications = "question_notifications";
                db.collection(users).document(firebaseUser.getUid()).collection(notifications)
                        .document(questionNotifications).update("newComment", b)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                notificationsLayout.setVisibility(View.VISIBLE);
                                progressBarNotifications.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                });
            }
        });

    }

    private void ratingSwitchChanged()
    {
        this.newRatingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                progressBarNotifications.setVisibility(View.VISIBLE);
                notificationsLayout.setVisibility(View.GONE);

                String notifications = "notifications";
                String questionNotifications = "question_notifications";
                db.collection(users).document(firebaseUser.getUid()).collection(notifications)
                        .document(questionNotifications).update("newRating", b)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                notificationsLayout.setVisibility(View.VISIBLE);
                                progressBarNotifications.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                    }
                });
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }
}