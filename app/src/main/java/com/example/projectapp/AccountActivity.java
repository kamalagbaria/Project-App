package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    private final String account = "Account";
    private Button personalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).setTitle(account);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.personalButton = findViewById(R.id.personal_information_button);

        this.pressedPersonalButton();
    }

    private void pressedPersonalButton()
    {
        this.personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }
}