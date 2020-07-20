package com.example.tiktok_mini;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktok_mini.player.VideoPlayerIJK;
import com.example.tiktok_mini.player.VideoPlayerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends AppCompatActivity {
    private VideoPlayerIJK player;
    private SeekBar seekBar;
    private TextView tv;
    private Timer timer;
    private boolean playing = true;
    private boolean isSeekBarChanging = false;

    @Override
    protected void onStart() {
        super.onStart();
        playing = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setTitle("播放器");

        player = findViewById(R.id.ijkPlayer);
        seekBar = findViewById(R.id.seekbar);
        tv = findViewById(R.id.textView);

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        Intent intent = getIntent();
        Bundle videoInfo = intent.getBundleExtra("data");
        player.setVideoPath(videoInfo.getString("feedurl"));
        player.setListener(new VideoPlayerListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                super.onPrepared(iMediaPlayer);
                seekBar.setMax((int)player.getDuration());
            }
        });

        GestureDetector detector = new GestureDetector(PlayerActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(PlayerActivity.this, "双击事件", Toast.LENGTH_SHORT).show();
                // 双击点赞一会再做
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (playing) {
                    player.pause();
                    playing = false;
                } else {
                    player.start();
                    playing = true;
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText(timeFormat(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                int progress = seekBar.getProgress();
                player.seekTo(progress);
            }
        });
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (playing && (!isSeekBarChanging)) {
                    seekBar.setProgress((int) player.getCurrentPosition());
                }
            }
        }, 0, 50);
    }

    protected String timeFormat(long millionSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onStop() {
        super.onStop();
        playing = false;
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (player!=null) {
            player.release();
        }
        player = null;
        IjkMediaPlayer.native_profileEnd();
    }
}
