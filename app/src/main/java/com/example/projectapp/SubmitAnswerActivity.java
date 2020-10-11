package com.example.projectapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.UUID;


public class SubmitAnswerActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE =123;
    Button imageButton;
    ImageView imageView;
    FirebaseStorage storage;
    StorageReference storageReference;
    String imageId=UUID.randomUUID().toString();
    private Uri filePath;
    private String questionKey;
    private String questionTitle;


    //Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String questionUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageButton=findViewById(R.id.imageButton);
        imageView=findViewById(R.id.image_answer);
        questionKey = getIntent().getStringExtra("question_key");
        questionTitle = getIntent().getStringExtra("question_title");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"pick an image"),GALLERY_REQUEST_CODE);
            }
        });
    }

    public void submitAnswer(View view){
        EditText answerET= findViewById(R.id.answerET);
        String text = answerET.getText().toString();
        String imageUrl="None";
        if (filePath!=null){
            imageUrl=imageId;
        }

        //final Answer answer = new Answer(text, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),imageUrl);
        final String questionKey = getIntent().getStringExtra("question_key");
        this.questionUserId = getIntent().getStringExtra("question_owner_id");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Answer answer = new Answer(text, user.getUid(), imageUrl,
               user.getDisplayName(),questionKey,questionTitle);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseDatabase.getInstance().getReference().child("answers").child(questionKey).push()
                .setValue(answer, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                        if (databaseError != null) {
                            Toast.makeText(SubmitAnswerActivity.this,"Unable to submit answer",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();


                            addAnswerToFirestore(reference.getKey(), questionKey, answer);
                            if(filePath == null){
                                //addAnswerToUser(answer);
                                //finish();

                            }else {
                                uploadImage();
                                //addAnswerToUser(answer);
                            }
                            addAnswerToUser(answer);
                            Toast.makeText(SubmitAnswerActivity.this,"Answer submitted",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });


    }

    private void addAnswerToFirestore(final String answerId, final String questionId, final Answer answer)
    {
        db.collection("users").document(questionUserId).collection("questions")
                .document(questionId).collection("answers").document(answerId).set(answer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                //good
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                //raise exception
            }
        });
           // finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            filePath=data.getData();
          //  imageView.setImageURI(filePath);
            Picasso.get().load(filePath).resize(2048, 1600)
                    .onlyScaleDown().into(imageView);
        }
    }
    private void addAnswerToUser(final Answer answer){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference().child("users").
                    child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user=snapshot.getValue(User.class);
                            if( user != null){
                                user.addNewAnswer(answer);
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser()
                                                .getUid()).child("UserAnswers").setValue(user.getUserAnswers())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
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

    private void uploadImage()
    {
        if (filePath != null) {
            final ProgressDialog progressDialog= new ProgressDialog(this);

            // Code for showing progressDialog while uploading
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/Answers/" + imageId);
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Toast.makeText(SubmitAnswerActivity.this,
                                            "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            Toast.makeText(SubmitAnswerActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
            progressDialog.dismiss();
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }
}
