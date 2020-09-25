package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalInfoActivity extends AppCompatActivity {


    private static final int CREDENTIAL_PICKER_REQUEST = 1;  // Set to an unused request code
    private final String PERSONAL_INFO = "Personal Information";
    private final String USERS = "users";
    private final String INFO_NOT_FULL = "Not Registered";
    private final String fullNameEmpty = "No Name registered, please add new";
    private final String emailEmpty = "No email registered, please add new";
    private final String phoneNumberEmpty = "No Phone registered, please add new";
    private final String locationEmpty = "No location registered, please add new";
    private final int linearLayoutID = 1234;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private TextView fullNameText;
    private TextView emailText;
    private TextView phoneNumberText;
    private TextView locationText;
    private ProgressBar progressBar;
    private LinearLayout personalInfoLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final int fullNameId = 0;
    private final int emailId = 1;
    private final int phoneNumberId = 2;
    private final int locationId = 3;

    private View.OnClickListener clickListener;

    private ArrayList<Pair<String, Integer>> errorMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        Objects.requireNonNull(getSupportActionBar()).setTitle(PERSONAL_INFO);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.fullNameText = findViewById(R.id.full_name);
        this.emailText = findViewById(R.id.email);
        this.phoneNumberText = findViewById(R.id.phone_number);
        this.locationText = findViewById(R.id.location);
        this.progressBar = findViewById(R.id.progress_bar_personal_info);
        this.personalInfoLayout = findViewById(R.id.personal_info_layout);

        //this.errorMessages = new ArrayList<>();

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.mAuth.getCurrentUser();

        this.initializeClickListener();

        if (this.firebaseUser == null)
        {
            personalInfoLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.request_sign_in_up).setVisibility(View.VISIBLE);
//            LinearLayout nameLayout = findViewById(R.id.name_layout);
//            LinearLayout emailLayout = findViewById(R.id.email_layout);
//            LinearLayout phoneNumberLayout = findViewById(R.id.phone_number_layout);
//            LinearLayout locationLayout = findViewById(R.id.location_layout);
//
//            nameLayout.setVisibility(View.GONE);
//            emailLayout.setVisibility(View.GONE);
//            phoneNumberLayout.setVisibility(View.GONE);
//            locationLayout.setVisibility(View.GONE);
//            findViewById(R.id.request_sign_in_up).setVisibility(View.VISIBLE);
        }
        else
        {
            this.getPersonalInfo();
            //addErrorMessages();
        }


    }

    private void initializeClickListener()
    {
        this.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View button)
            {
                switch (button.getId())
                {
                    case phoneNumberId:
                        updatePhoneNumber();
                        break;

                    case locationId:
                        updateLocation();
                        break;

                    case fullNameId:
                        updateFullName();
                        break;

                    case emailId:
                        updateEmail();
                        break;
                }
            }
        };
    }

    private void addErrorMessages()
    {
        for(Pair<String, Integer> error :  this.errorMessages)
        {
            this.createLayout(error);
        }
    }

    private void createLayout(Pair<String, Integer> error)
    {
        //parent is personal info layout
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.error_message, null, false);
        linearLayout.setId(linearLayoutID);

        final float scale = getResources().getDisplayMetrics().density;
        int padding_10dp = (int) (10 * scale + 0.5f);

        linearLayout.setPadding(0,padding_10dp,0,0);

        TextView errorMessage = linearLayout.findViewById(R.id.error_text);
        linearLayout.findViewById(R.id.error_button).setId(error.second);
        linearLayout.findViewById(error.second).setOnClickListener(this.clickListener);
        errorMessage.setText(error.first);


        this.personalInfoLayout.addView(linearLayout);

    }

    private void removeLayout()
    {
        LinearLayout linearLayout = findViewById(linearLayoutID);
        if(linearLayout == null)
        {
            return;
        }
        linearLayout.removeAllViews();
        this.personalInfoLayout.removeView(linearLayout);
    }


    private void getPersonalInfo()
    {
        DocumentReference documentReference = this.db.collection(USERS).document(this.firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    errorMessages = new ArrayList<>();
                    DocumentSnapshot document = task.getResult();
                    if(document != null)
                    {
                        loadFullName(document.getString("fullName"));
                        loadEmail(document.getString("email"));
                        loadPhoneNumber(document.getString("phoneNumber"));
                        loadLocation(document.getString("location"));
                        removeLayout();
                        addErrorMessages();
                    }
                    progressBar.setVisibility(View.GONE);
                    personalInfoLayout.setVisibility(View.VISIBLE);
                    errorMessages.clear();
                }
            }
        });
    }


    private void loadFullName(String fullName)
    {
        if("".equals(fullName) || fullName == null)
        {
            this.fullNameText.setText(INFO_NOT_FULL);
            this.fullNameText.setTextColor(Color.RED);
            this.errorMessages.add(new Pair<>(fullNameEmpty, fullNameId));
        }
        else
        {
            this.fullNameText.setText(fullName);
            this.fullNameText.setTextColor(Color.BLACK);

        }
    }

    private void loadEmail(String email)
    {
        if("".equals(email) || email == null)
        {
            this.emailText.setText(INFO_NOT_FULL);
            this.emailText.setTextColor(Color.RED);
            this.errorMessages.add(new Pair<>(emailEmpty, emailId));
        }
        else
        {
            this.emailText.setText(email);
            this.emailText.setTextColor(Color.BLACK);
        }
    }

    private void loadPhoneNumber(String phoneNumber)
    {
        if("".equals(phoneNumber) || phoneNumber == null)
        {
            this.phoneNumberText.setText(INFO_NOT_FULL);
            this.phoneNumberText.setTextColor(Color.RED);
            this.errorMessages.add(new Pair<>(phoneNumberEmpty, phoneNumberId));
        }
        else
        {
            this.phoneNumberText.setText(phoneNumber);
            this.phoneNumberText.setTextColor(Color.BLACK);
        }
    }

    private void loadLocation(String location)
    {
        if("".equals(location) || location == null)
        {
            this.locationText.setText(INFO_NOT_FULL);
            this.locationText.setTextColor(Color.RED);
            this.errorMessages.add(new Pair<>(locationEmpty, locationId));
        }
        else
        {
            this.locationText.setText(location);
            this.locationText.setTextColor(Color.BLACK);
        }
    }

    private void updateFullName()
    {
        openAlertDialog("Full Name", "Other users will see this information", fullNameId);
    }

    private void updateEmail()
    {

    }

    private void updatePhoneNumber()
    {
        openAlertDialog("Phone Number", "Please use 012-3456789 format", phoneNumberId);
    }

    private void updateLocation()
    {
        openAlertDialog("Location", "Still", locationId);
    }

    private void openAlertDialog(String title, String message, final int id)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setTitle("Enter Your " + title);
        alert.setMessage(message);

        alert.setView(edittext);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String textValue = edittext.getText().toString();
                if(id == fullNameId)
                {
                    boolean check = checkFullName(textValue);
                    if(check)
                    {
                        updateFirestore("fullName", textValue);

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Bad Name", Toast.LENGTH_SHORT).show();
                    }
                }
                if(id == phoneNumberId)
                {
                    boolean check = checkPhoneNumber(textValue);
                    if(check)
                    {
                        updateFirestore("phoneNumber", textValue);
                        Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Bad", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }

    private void updateFirestore(String fieldName, String fieldNewValue)
    {
        progressBar.setVisibility(View.VISIBLE);
        personalInfoLayout.setVisibility(View.GONE);
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, fieldNewValue);
        db.collection(USERS).document(this.firebaseUser.getUid())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        removeLayout();
                        getPersonalInfo();
                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error in Updating", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean checkPhoneNumber(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("\\d{3}-\\d{7}");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private boolean checkFullName(String fullName)
    {
        for(int i = 0; i < fullName.length(); i++)
        {
            char c = fullName.charAt(i);
            if(!(Character.isLetter(c) || c == '-' || c == ' '))
            {
                return false;
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onBackPressed();
        return true;
    }

}