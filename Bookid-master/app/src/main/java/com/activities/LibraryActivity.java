package com.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.models.VideoBook;
import com.controllers.VideoBookAdapter;
import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements VideoBookAdapter.OnItemClickListener   {

    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private ProgressBar mProgressCircle;
    private RecyclerView mRecyclerView;
    private VideoBookAdapter mAdapter;
    private List<VideoBook> mBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("library");
        mProgressCircle = findViewById(R.id.progress_circle);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mBooks = new ArrayList<>();
        mAdapter = new VideoBookAdapter(LibraryActivity.this, mBooks);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.onItemClickListener(LibraryActivity.this);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBooks.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    VideoBook book = postSnapshot.getValue(VideoBook.class);
                    book.setKey(postSnapshot.getKey());
                    mBooks.add(book);
                }

                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LibraryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void backToMain(View view) {
        Intent openMainActivity = new Intent(this, MainActivity.class);
        startActivity(openMainActivity);
    }

    @Override
    public void onItemClick(int position) {
        Intent openVideoActivity = new Intent(this, VideoActivity.class);
        openVideoActivity.putExtra("uri", mBooks.get(position).getVideo());
        startActivity(openVideoActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
