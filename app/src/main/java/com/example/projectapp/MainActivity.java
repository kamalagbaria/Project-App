package com.example.projectapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private static String name;
    private static Uri photoUrl;
    private static NavigationView navigationView;
    private static TextView barTitle;

    //To Create Notification Channel
    private static final String CHANNEL_ID = "APP_PROJECT";
    private static final String NOTIFICATION_TITLE = "PROJECT_APP";
    private SendNotificationBroadcastReceiver br;

    private DatabaseReference mDatabase;

    FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SubscriptionToTopic subscriptionToTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        this.subscriptionToTopic = new SubscriptionToTopic();

        subscribeToTopicId();

        //Create Notification Channel
        this.createNotificationChannel();
        this.br = new SendNotificationBroadcastReceiver();
        registerReceiver(this.br, new IntentFilter(SendNotificationBroadcastReceiver.actionQuestionAnswered));
        registerReceiver(this.br, new IntentFilter(SendNotificationBroadcastReceiver.actionQuestionRated));
        //this.listenToDataChangeRealTime();
        //this.listenToDataChangeFirestore();

        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        barTitle = findViewById(R.id.textTitle);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController= Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);

    }

    public void subscribeToTopicId()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(user.getUid()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful())
                            {
                                //handle error
                            }

                        }
                    });
        }

    }


    private void listenToDataChangeRealTime()
    {
        //might need to use ValueEventListener
        mDatabase.child("answers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int x = 0;
                //Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                sendBroadcast(new Intent(SendNotificationBroadcastReceiver.actionQuestionAnswered));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_TITLE, importance);
            channel.setDescription(NOTIFICATION_TITLE);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
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
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
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
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            updateUserImage(user);
        }
        else {
            resetProfilePic();
        }
    }

    protected static void setBarText(String fragment_name){
        barTitle.setText(fragment_name);
    }
}
