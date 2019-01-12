package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.controllers.ImageAdapter;
import com.models.Upload;
import com.google.android.gms.samples.vision.ocrreader.OcrCaptureActivity;
import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookListActivity extends Activity implements ImageAdapter.OnItemClickListener {

    ImageView imgTakenPic;
    private static final int CAM_REQUEST=1313;

    private TextToSpeech mTTS;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private ProgressBar mProgressBar;
    private ProgressBar mProgressCircle;
    private StorageTask mUploadTask;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        //initialize the text ot speak engine
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTTS.setLanguage(Locale.UK);
                }
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("uploads");
        mProgressCircle = findViewById(R.id.progress_circle);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Drawable progressDrawable = mProgressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        mProgressBar.setProgressDrawable(progressDrawable);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(BookListActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.onItemClickListener(BookListActivity.this);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mUploads.add(new Upload("plusBtn", "https://firebasestorage.googleapis.com/v0/b/bookid-ca15f.appspot.com/o/plus.png?alt=media&token=150e12ab-5fa2-4346-906d-8423f7091109"));

                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void captureCameraImage() {
        if(mUploadTask != null && mUploadTask.isInProgress()){
            Toast.makeText(BookListActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAM_REQUEST);
        }
    }

    private void speak(String textToSpeak) {
        mTTS.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        imgTakenPic  = (ImageView)findViewById(R.id.imageViewForBookCover);
        if(requestCode == CAM_REQUEST) {
            mRecyclerView.setVisibility(View.GONE);
            imgTakenPic.setVisibility(View.VISIBLE);

            bitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(bitmap);

            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmap)                 // your image bitmap
                    .build();

            String imageText = "";

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                imageText = textBlock.getValue();                   // return string
            }

            //prepare file and upload it
            Bitmap bitmapToUpload = ((BitmapDrawable) imgTakenPic.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapToUpload.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataToUpload = baos.toByteArray();
            if(imageText == "" || imageText == null){imageText = "NoName"; }

            uploadFile(dataToUpload, imageText);
        }
    }

    private void uploadFile(byte[] bookImg, final String bookName){
        if (bookImg != null) {
            //uploading the file
            StorageReference fileRef = mStorageRef.child(bookName + "." + System.currentTimeMillis());
            mProgressBar.setVisibility(View.VISIBLE);
            mUploadTask = fileRef.putBytes(bookImg)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    imgTakenPic.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                }
                            }, 5000);

                            Toast.makeText(BookListActivity.this, "Upload Success", Toast.LENGTH_LONG).show();

                            //add entry to current pic in db
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Upload upload = new Upload(bookName, uri.toString());
                                            String uploadId = mDatabaseRef.push().getKey(); //creates new entry on db
                                            mDatabaseRef.child(uploadId).setValue(upload); //saves the file metadata on the new entry
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //TODO: fail to get url
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BookListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "NoFile", Toast.LENGTH_LONG).show();
        }

    } //uploadFile()

    @Override
    public void onItemClick(int position) {
        if(position == mUploads.size() - 1){
            speak("please take a picture of the book you want to add");
            captureCameraImage();
        }
        else {
            speak("for scanning, follow the book you have choose with the camera");
            Intent openOcrActivity = new Intent(this, OcrCaptureActivity.class);
            startActivity(openOcrActivity);
        }
    }

    @Override
    public void onCheckClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();
        boolean newValue = !selectedItem.isCheck();

        selectedItem.setCheck(newValue);
        mDatabaseRef.child(selectedKey).child("check").setValue(newValue);
    }

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedItem.getImageUrl());
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(BookListActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

    }

    public void backToMain(View view) {
        Intent openMainActivity = new Intent(this, MainActivity.class);
        startActivity(openMainActivity);
    }
}
