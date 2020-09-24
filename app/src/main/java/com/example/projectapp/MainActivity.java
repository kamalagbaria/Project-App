package com.example.projectapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static String name;
    private static Uri photoUrl;
    private static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController= Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Adapter.cleanup();
    }

    public static void resetProfilePic(){
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageProfile);
        username.setText("User");
        navUserPhoto.setImageResource(R.drawable.profile_pic);

    }

    public static void updateUserImage(FirebaseUser user){
        View headerView = navigationView.getHeaderView(0);
        final ImageView navUserPhoto = headerView.findViewById(R.id.imageProfile);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        updateUserName(user);
        photoUrl = user.getPhotoUrl();
        StorageReference profileRef = storageReference.child("profilePictures/"+
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).resize(2048, 1600)
                        .onlyScaleDown().into(navUserPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(photoUrl != null) {
                    Picasso.get().load(photoUrl).into(navUserPhoto);
                }
            }
        });

    }

    public static void updateUserName(FirebaseUser user){
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        name = user.getDisplayName();
        username.setText(name);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            updateUserImage(user);
        }
        else {
            resetProfilePic();
        }
    }
}
