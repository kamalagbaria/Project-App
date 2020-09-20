package com.example.projectapp;

import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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

    public static void changeProfilePic(){
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageProfile);
        username.setText("User");
        navUserPhoto.setImageResource(R.drawable.profile_pic);

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        ImageView navUserPhoto = headerView.findViewById(R.id.imageProfile);
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                name = profile.getDisplayName();
                photoUrl = profile.getPhotoUrl();
            }
            username.setText(name);
            if(photoUrl != null) {
                Picasso.get().load(photoUrl).into(navUserPhoto);
            }
        }
        else {
            changeProfilePic();
        }
    }
}
