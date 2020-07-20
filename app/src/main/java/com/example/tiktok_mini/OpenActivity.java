package com.example.tiktok_mini;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class OpenActivity extends AppCompatActivity {

    private ImageView openImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        openImage = findViewById(R.id.openImage);
        openImage.setImageResource(R.drawable.openlogo);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(OpenActivity.this,MainActivity.class);
                OpenActivity.this.finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fade_out,R.anim.fade_in);
            }
        };
        handler.postDelayed(runnable,2000);
    }
}
