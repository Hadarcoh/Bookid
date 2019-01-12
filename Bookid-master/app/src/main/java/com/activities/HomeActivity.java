package com.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.dbHandler.CountryData;
import com.models.User;
import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.dbHandler.DatabaseApi;

public class HomeActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDbUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, CountryData.countryNames));

        editText = findViewById(R.id.editTextMobile);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid Number is required");
                    editText.requestFocus();
                    return;
                }

                String phoneNumber = "+" + code + number;

                Intent intent = new Intent(HomeActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth=FirebaseAuth.getInstance();
        mDbUsersRef = FirebaseDatabase.getInstance().getReference().child("users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    //user is signed in
                    mDbUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!DatabaseApi.getInstance().checkIfUserExistInDb(user.getUid(), dataSnapshot)) {
                                //add user to db
                                mDbUsersRef.child(user.getUid()).setValue(new User(user.getUid(), user.getPhoneNumber()));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    //user signed out
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener); //firebaseAuth is of class FirebaseAuth

        if (FirebaseAuth.getInstance().getCurrentUser() !=null)
        {
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}