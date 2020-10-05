package com.example.projectapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    //Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        imageButton=findViewById(R.id.imageButton);
        imageView=findViewById(R.id.image_answer);

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

        final Answer answer = new Answer(text, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),imageUrl);
        final String questionKey = getIntent().getStringExtra("question_key");
        this.userId = getIntent().getStringExtra("question_owner_id");
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
                            Toast.makeText(SubmitAnswerActivity.this,"Answer submitted",
                                    Toast.LENGTH_SHORT).show();

                            addAnswerToFirestore(reference.getKey(), questionKey, answer);
                            //finish();
                        }
                    }
                });


    }

    private void addAnswerToFirestore(final String answerId, final String questionId, final Answer answer)
    {
        //Keep the Thread version in case

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                db.collection("users").document(userId).collection("questions")
//                        .document(questionId).collection("answers").document(answerId).set(answer).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid)
//                    {
//                        //good
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e)
//                    {
//                        //raise exception
//                    }
//                });
//            }
//        }).start();
        db.collection("users").document(userId).collection("questions")
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            filePath=data.getData();
            imageView.setImageURI(filePath);
        }
    }
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
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
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(SubmitAnswerActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(SubmitAnswerActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
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
        }
    }
}
