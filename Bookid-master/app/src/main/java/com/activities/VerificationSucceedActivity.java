package com.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.samples.vision.ocrreader.R;

import java.util.Timer;
import java.util.TimerTask;

public class VerificationSucceedActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_succeed);

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent= new Intent(VerificationSucceedActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
