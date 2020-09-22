package com.example.projectapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    private Activity activity;
    private static final int RC_SIGN_IN = 1;
    private final static String users = "users";
    private Button buttonSignIn;
    private Button buttonSignOut;
    private Button buttonQuestions;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView fullName;
    private TextView email;
    private ImageView profile_image;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        this.activity = getActivity();

        this.providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        this.buttonSignIn = root.findViewById(R.id.button_sign_in);
        this.buttonSignOut = root.findViewById(R.id.button_sign_out);
        this.profile_image = root.findViewById(R.id.image_user_profile_image);
        this.fullName = root.findViewById(R.id.fullName);
        this.email = root.findViewById(R.id.text_user_profile_email);
        this.buttonQuestions = root.findViewById(R.id.QuestionsButton);

        this.mAuth = FirebaseAuth.getInstance();

        this.listenForAuth();
        this.ReviewQuestion();
        this.buttonSignInPressed();
        this.buttonSignOutPressed();

        return root;
    }
    private void listenForAuth()
    {
        this.mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    //Is logged in
                    ProfileFragment.this.buttonSignIn.setVisibility(View.INVISIBLE);
                    ProfileFragment.this.buttonSignOut.setVisibility(View.VISIBLE);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateProfile(user);
                } else {
                    //Is logged out
                    ProfileFragment.this.buttonSignOut.setVisibility(View.INVISIBLE);
                    ProfileFragment.this.buttonSignIn.setVisibility(View.VISIBLE);
                    profile_image.setVisibility(INVISIBLE);
                    fullName.setVisibility(INVISIBLE);
                    email.setVisibility(INVISIBLE);
                    buttonQuestions.setVisibility(INVISIBLE);

                    MainActivity.changeProfilePic();

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
                ProfileFragment.this.signOut();

            }
        });

    }

    private void buttonSignInPressed()
    {
        this.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.this.signIn();
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
                        .setTheme(R.style.GreenTheme)
                        .build(),
                RC_SIGN_IN);
    }

    private void signOut()
    {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(activity, "Signed-Out Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfile(FirebaseUser user){
        Uri photoUrl = null;
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                photoUrl = profile.getPhotoUrl();
            }
            if(photoUrl != null) {
                Picasso.get().load(photoUrl).into(profile_image);
            }
            //  profile_image.setVisibility(INVISIBLE);
            fullName.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
    }

    public void ReviewQuestion(){
        if(mAuth.getCurrentUser() != null) {
            this.buttonQuestions.setVisibility(VISIBLE);
            buttonQuestions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), UserQuestionList.class);
                    // i.putExtra("question_key", getIntent().getStringExtra("question_key"));
                    startActivity(i);
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                addUserToFireStore(user);
                Toast.makeText(activity, "Signed-In Successfully", Toast.LENGTH_SHORT).show();
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
        User newUser = new User(user.getDisplayName(), "", "", user.getEmail(), "", user.getUid());
        db.collection(users).document(newUser.getId()).set(newUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mAuth.addAuthStateListener(this.mAuthStateListener);
    }

}