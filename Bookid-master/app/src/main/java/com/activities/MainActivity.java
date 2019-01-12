package com.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.samples.vision.ocrreader.OcrCaptureActivity;
import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends Activity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private DatabaseReference mDatabase;
    private TextToSpeech mTTS;
    private Button start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start_button = findViewById(R.id.start_button);

        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        checkCameraPermission();

        //initialize the text ot speak engine
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTTS.setLanguage(Locale.UK);
                }
            }
        });

    }



    private void speak(String textToSpeak) {
        mTTS.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    public void openToReadListActivity(View view)
    {
        Intent openToReadListActivity = new Intent(this, BookListActivity.class);
        startActivity(openToReadListActivity);
    }


    public void openOcrActivity(View view) {
        speak("for scanning, follow the book with the camera");
        Intent openOcrActivity = new Intent(this, OcrCaptureActivity.class);
        startActivity(openOcrActivity);

    }

    public void openLibrary(View view) {
        Intent openLibraryActivity = new Intent(this, LibraryActivity.class);
        startActivity(openLibraryActivity);
    }

    public void appInformation(View view){
        speak("welcome to bookid. in order to start reading a new book ,press on the blue button. in order to create your own book list, press on the pink button. and in order to see more books, press on the orange button." +
                "Enjoy your book!");
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

}
