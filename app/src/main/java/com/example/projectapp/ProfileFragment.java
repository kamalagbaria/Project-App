package com.example.projectapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

    private static final int GALLERY_REQUEST_CODE =100;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView fullName;
    private TextView email;
    private ImageView profile_image;
    private ImageButton name_image;
    private ImageView mail_image;
    private ImageView list_image;
    private ImageButton editBtn ;
    FirebaseStorage storage;
    StorageReference storageReference;

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
        this.editBtn = root.findViewById(R.id.edit_profilePicture);
        this.name_image = root.findViewById(R.id.imageName);
        this.mail_image = root.findViewById(R.id.imageMail);
        this.list_image = root.findViewById(R.id.imagelist);

        this.mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        this.listenForAuth();
        this.ReviewQuestion();
        this.buttonSignInPressed();
        this.buttonSignOutPressed();
        this.buttonEditPicture();
        this.updateUserName();
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
                    editBtn.setVisibility(INVISIBLE);
                    mail_image.setVisibility(INVISIBLE);
                    name_image.setVisibility(INVISIBLE);
                    list_image.setVisibility(INVISIBLE);

                    MainActivity.resetProfilePic();

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
    private void buttonEditPicture(){
        this.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectPicture
                Intent openGallery = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, GALLERY_REQUEST_CODE);
            }
        });
    }
    private void updateUserName(){
        name_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.change_user_name, null);

                final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
                Button submitBtn = (Button) dialogView.findViewById(R.id.buttonSubmit);
                Button cancelBtn = (Button) dialogView.findViewById(R.id.buttonCancel);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(editText.getText().toString())
                                .build();
                        user.updateProfile(profileUpdates).addOnSuccessListener
                                (new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.updateUserName(user);
                            }
                        });
                        fullName.setText(editText.getText());
                        dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
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

    private void updateProfile(final FirebaseUser user){
        StorageReference profileRef = storageReference.child("profilePictures/"+
                mAuth.getCurrentUser().getUid());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(2048, 1600)
                        .onlyScaleDown().into(profile_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Uri photoUrl = null;
                for (UserInfo profile : user.getProviderData()) {
                    photoUrl = profile.getPhotoUrl();
                }
                if(photoUrl != null){
                    Picasso.get().load(photoUrl).into(profile_image);
                }
            }
        });
        profile_image.setVisibility(VISIBLE);
        fullName.setVisibility(VISIBLE);
        fullName.setText(user.getDisplayName());
        email.setText(user.getEmail());
        email.setVisibility(VISIBLE);
        buttonQuestions.setVisibility(VISIBLE);
        editBtn.setVisibility(VISIBLE);
        mail_image.setVisibility(VISIBLE);
        name_image.setVisibility(VISIBLE);
        list_image.setVisibility(VISIBLE);
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
        if(requestCode == GALLERY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                uploadPicture(imageUri);
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

    private void uploadPicture(final Uri imgUri){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final StorageReference fileRef = storageReference.
                child("profilePictures/"+mAuth.getCurrentUser().getUid());
        fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getActivity(), "Image Uploaded!!",
                                        Toast.LENGTH_SHORT).show();
                        Picasso.get().load(uri).resize(2048, 1600)
                                .onlyScaleDown().into(profile_image);
                        progressDialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        MainActivity.updateUserImage(user);
                 }
        });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            updateProfile(user);
        }
    }
}