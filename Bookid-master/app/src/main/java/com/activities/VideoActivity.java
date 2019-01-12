package com.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.samples.vision.ocrreader.R;

public class VideoActivity extends AppCompatActivity {

    private WebView mWevView;
    private VideoView mVideoView;
    private Button mBtnPlay, mBtnReplay;
    private ProgressBar mProgressBar;

    private Uri mUri;
    private boolean isOn = false;
    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mBtnPlay = (Button)findViewById(R.id.play_btn);
        mBtnReplay = (Button) findViewById(R.id.replay_btn);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_circle);

        Intent intent = getIntent();
        String str = intent.getStringExtra("uri");
        final Uri mUri = Uri.parse(str);

        mVideoView.setVideoURI(mUri);
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                isFirstTime = true;
                mVideoView.stopPlayback();
                mBtnPlay.setBackground(getResources().getDrawable(R.drawable.play));
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            final Uri uri = mUri;

            @Override
            public void onClick(View v) {
                if(isOn){
                    mVideoView.pause();
                    mBtnPlay.setBackground(getResources().getDrawable(R.drawable.play));
                } else {
                    mBtnPlay.setBackground(getResources().getDrawable(R.drawable.pause));

                    if(isFirstTime) {
                        mVideoView.setVideoURI(uri);
                        mVideoView.requestFocus();
                        mVideoView.start();
                        mProgressBar.setVisibility(View.VISIBLE);
                        isFirstTime = false;
                    }
                    else {
                        mVideoView.resume();
                    }
                }
                isOn = !isOn;
            }
        });

        mBtnReplay.setOnClickListener(new View.OnClickListener() {
            final Uri uri = mUri;

            @Override
            public void onClick(View v) {
                if(mVideoView != null) {
                    mVideoView.stopPlayback();
                    mBtnPlay.setBackground(getResources().getDrawable(R.drawable.pause));

                    mVideoView.setVideoURI(uri);
                    mVideoView.requestFocus();
                    mVideoView.start();
                    mProgressBar.setVisibility(View.VISIBLE);
                    isFirstTime = false;
                }
            }
        });
    }
}
