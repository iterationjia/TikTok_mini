package com.example.tiktok_mini;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
    private TextView likeNumTv;
    private ImageView likeView;
    private ImageView avatar;
    private Timer timer;
    private boolean playing = true;
    private boolean isSeekBarChanging = false;
    private boolean like = false;
    private int likeNum;

    @Override
    protected void onStart() {
        super.onStart();
        playing = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        player = findViewById(R.id.ijkPlayer);
        seekBar = findViewById(R.id.seekbar);
        tv = findViewById(R.id.textView);
        likeNumTv = findViewById(R.id.like_num);
        likeView = findViewById(R.id.like_img);
        avatar = findViewById(R.id.avatar);

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        Intent intent = getIntent();
        Bundle videoInfo = intent.getBundleExtra("data");

        likeNum = videoInfo.getInt("likeNum");
        likeNumTv.setText(likeNumFormat(likeNum));
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform();
        Glide.with(this).load(videoInfo.getString("avatarUrl")).apply(mRequestOptions).into(avatar);

        player.setVideoPath(videoInfo.getString("feedurl"));
        player.setListener(new VideoPlayerListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                super.onPrepared(iMediaPlayer);
                seekBar.setMax((int)player.getDuration());
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                Log.d("change", "aaa");
                super.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3);
                player.changeSurfaceView();
            }
        });

        GestureDetector detector = new GestureDetector(PlayerActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                likeChange();
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

    private void likeChange() {
        if (like) {
            like = false;
            likeNum--;
            likeView.setImageDrawable(getDrawable(R.drawable.unlike));
            Toast.makeText(PlayerActivity.this, "取消喜欢", Toast.LENGTH_SHORT).show();
        } else {
            like = true;
            likeNum++;
            likeView.setImageDrawable(getDrawable(R.drawable.like));
            Toast.makeText(PlayerActivity.this, "喜欢", Toast.LENGTH_SHORT).show();
        }
        likeNumTv.setText(likeNumFormat(likeNum));
    }

    private String likeNumFormat(int num) {
        if (num < 1000) {
            return String.valueOf(num);
        } else if (num <= 1000000) {
            return String.valueOf(num / 1000) + '.' + String.valueOf((num % 1000) / 100) + 'k';
        } else {
            return String.valueOf(num / 1000000) + '.' + String.valueOf((num % 1000) / 100000) + 'm';
        }
    }

    protected String timeFormat(long millionSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playing = false;
        player.stop();
        timer.cancel();
        if (player!=null) {
            player.release();
        }
        player = null;
        IjkMediaPlayer.native_profileEnd();
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
