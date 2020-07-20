package com.example.tiktok_mini;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private MyAdapter myAdapter;
    private ViewPager2 viewPager2;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        viewPager2 = findViewById(R.id.viewpage2);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);

        animationView = findViewById(R.id.animation_load);

        // specify an adapter (see also next example)
        myAdapter = new MyAdapter();

        //recyclerView.setAdapter(myAdapter);
        viewPager2.setAdapter(myAdapter);
        viewPager2.setAlpha(0f);

        getData();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getData();
                ObjectAnimator alphaAnimator_out = ObjectAnimator.ofFloat(animationView,"alpha",1f,0f);
                alphaAnimator_out.setInterpolator(new LinearInterpolator());
                alphaAnimator_out.setDuration(2000);

                ObjectAnimator alphaAnimator_in = ObjectAnimator.ofFloat(viewPager2,"alpha",0f,1f);
                alphaAnimator_in.setInterpolator(new LinearInterpolator());
                alphaAnimator_in.setDuration(2000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(alphaAnimator_out,alphaAnimator_in);
                animatorSet.start();
            }
        };
        handler.postDelayed(runnable,2000);
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        apiService.getVideos().enqueue(new Callback<List<VideoResponse>>() {
            @Override
            public void onResponse(Call<List<VideoResponse>> call, Response<List<VideoResponse>> response) {
                if (response.body() != null) {
                    List<VideoResponse> videos = response.body();
                    Log.d("retrofit", videos.toString());
                    if (videos.size() != 0) {
                        myAdapter.setData(response.body(),MainActivity.this);
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VideoResponse>> call, Throwable t) {
                Log.d("retrofit",t.getMessage());
            }
        });

    }
}