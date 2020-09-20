package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private final static String users = "users";
    private Button buttonSignIn;
    private Button buttonSignOut;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        this.providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        this.buttonSignIn = findViewById(R.id.button_sign_in);
        this.buttonSignOut = findViewById(R.id.button_sign_out);
        this.mAuth = FirebaseAuth.getInstance();

        this.listenForAuth();
        this.buttonSignInPressed();
        this.buttonSignOutPressed();

    }

    private void listenForAuth()
    {
        this.mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null)
                {
                    //Is logged in
                    ProfileActivity.this.buttonSignIn.setVisibility(View.INVISIBLE);
                    ProfileActivity.this.buttonSignOut.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "1", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    //Is logged out
                    ProfileActivity.this.buttonSignOut.setVisibility(View.INVISIBLE);
                    ProfileActivity.this.buttonSignIn.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "2", Toast.LENGTH_SHORT).show();
                }

            }
        };


    }

    private void buttonSignOutPressed()
    {
        this.buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ProfileActivity.this.signOut();
            }
        });

    }

    private void buttonSignInPressed()
    {
        this.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.signIn();
            }
        });

    }

    private void signIn()
    {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false, true)
                        .setAvailableProviders(this.providers)
                        .build(),
                RC_SIGN_IN);

    }

    private void signOut()
    {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                addUserToFireStore(user);
                Toast.makeText(ProfileActivity.this, "Reached here", Toast.LENGTH_SHORT).show();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void addUserToFireStore(FirebaseUser user)
    {
        User newUser = new User(user.getDisplayName(), user.getDisplayName(), "", user.getEmail(), "", user.getUid());
        db.collection(users).document(newUser.getId()).set(newUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }
}