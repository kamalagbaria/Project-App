package PostPc.Ask.projectapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserAnswerList extends AppCompatActivity {

    private ListView answerList;
    private UserAnswersAdapter Adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_answer_list);

        answerList = findViewById(R.id.userAnswers);
        mAuth = FirebaseAuth.getInstance();
        showAnswerList();
    }

    public void showAnswers(final ArrayList<Answer> answers){
        Adapter = new UserAnswersAdapter(UserAnswerList.this,0,answers);
        answerList.setAdapter(Adapter);
        answerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(UserAnswerList.this)
                        .setTitle("Delete Answer")
                        .setMessage("Are you sure you want to delete this answer?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query Query = ref.child("answers").
                                        child(Objects.requireNonNull(answers.get(position).getKey()));
                                Query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                                            Answer answer = Snapshot.getValue(Answer.class);
                                            assert answer != null;
                                            if(answers.get(position).getAnswerId().equals(answer.getAnswerId())){
                                                Snapshot.getRef().removeValue();
                                            }
                                        }
                                        deleteAnswerFormUserAnswers(position);
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

    public void showAnswerList(){
        if (mAuth.getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().
                    getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    if(user!=null){

                        ArrayList<Answer> answers = user.getUserAnswers();
                        showAnswers(answers);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void deleteAnswerFormUserAnswers(final int position){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    if(user!=null){
                        final ArrayList<Answer> answers = user.getUserAnswers();
                        answers.remove(position);
                        mDatabase.child("UserAnswers").setValue(answers).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UserAnswerList.this,
                                        "Deleted answer", Toast.LENGTH_SHORT).show();
                                showAnswers(answers);
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

    @Override
    public void onResume() {
        super.onResume();
        showAnswerList();
    }
}