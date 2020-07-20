package com.example.tiktok_mini;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktok_mini.player.VideoPlayerIJK;
import com.example.tiktok_mini.player.VideoPlayerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends AppCompatActivity {
    private VideoPlayerIJK player;
    private SeekBar seekBar;
    private TextView tv;
    private boolean playing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setTitle("播放器");

        player = findViewById(R.id.ijkPlayer);

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        player.setListener(new VideoPlayerListener());
        Intent intent = getIntent();
        Bundle videoInfo = intent.getBundleExtra("data");
        player.setVideoPath(videoInfo.getString("feedurl"));

        seekBar = findViewById(R.id.seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser&&player.isPlaying()){
                    player.seekTo(seekBar.getProgress()*player.getDuration()/100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress()*player.getDuration()/100);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress()*player.getDuration()/100);
            }
        });

        tv = findViewById(R.id.textView);

        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        tv.setText(timeFormat(player.getCurrentPosition())); //更新时间
                        if(player.getDuration()!=0) seekBar.setProgress((int)(player.getCurrentPosition()*100/player.getDuration()));
                        break;
                    default:
                        break;

                }
            }
        };

        class TimeThread extends Thread {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 1;  //消息(一个整型值)
                        mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }

        new TimeThread().start();

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playing) {
                    player.pause();
                    playing = false;
                } else {
                    player.start();
                    playing = true;
                }
            }
        });
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
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
