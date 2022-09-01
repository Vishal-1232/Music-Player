package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class playSong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
        barVisualizer.release();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    TextView textView,s,e;
    ImageView play,previous,next,imageView,fastForward,fastRewind;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textcontent;
    int position;
    Thread updateSeek;
    SeekBar seekBar;
    Thread seek;
    BarVisualizer barVisualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        getSupportActionBar().setTitle("Melody Muzik");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        imageView = findViewById(R.id.imageView);
        s = findViewById(R.id.t1);
        e = findViewById(R.id.t2);
        fastForward = findViewById(R.id.ff);
        fastRewind = findViewById(R.id.fr);
        barVisualizer = findViewById(R.id.wave);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textcontent = intent.getStringExtra("currentSong");
        textView.setText(textcontent);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration())
                    {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        String endtime = createTime(mediaPlayer.getDuration());
        e.setText(endtime);


        final int delay = 1000;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    s.setText(currentTime);
                    handler.postDelayed(this,delay);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },delay);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();

                    // Animation
                    TranslateAnimation moveAnim = new TranslateAnimation(-25,25,-25,25);
                    moveAnim.setInterpolator(new AccelerateInterpolator());
                    moveAnim.setDuration(600);
                    moveAnim.setFillEnabled(true);
                    moveAnim.setFillAfter(true);
                    moveAnim.setRepeatMode(Animation.REVERSE);
                    moveAnim.setRepeatCount(1);
                    imageView.startAnimation(moveAnim);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation(imageView,-360f);
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0)
                {
                    position--;
                }
                else{
                    position = songs.size()-1;
                }
                textcontent = songs.get(position).getName().toString();
                textView.setText(textcontent);

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);

                String endtime = createTime(mediaPlayer.getDuration());
                e.setText(endtime);

                if(mediaPlayer.isPlaying()){
                    visualizer();
                }
                else{
                    barVisualizer.release();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation(imageView,360f);
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size()-1)
                {
                    position++;
                }
                else{
                    position = 0;
                }
                textcontent = songs.get(position).getName();
                textView.setText(textcontent);

                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);

                String endtime = createTime(mediaPlayer.getDuration());
                e.setText(endtime);

                if(mediaPlayer.isPlaying()){
                    visualizer();
                }
                else{
                    barVisualizer.release();
                }


            }
        });

        fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });
        fastRewind.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        visualizer();



    }
    public void startAnimation(View view, Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time + min + ":";

        if (sec < 10){
            time += "0";
        }
        time += sec;

        return time;
    }
    public void visualizer(){
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1){
            barVisualizer.setAudioSessionId(audioSessionId);
        }
    }
}