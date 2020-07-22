package com.example.tiktok_mini;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton recordBtn;
    private MyAdapter myAdapter;
    private ViewPager2 viewPager2;
    private LottieAnimationView mainLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordBtn = findViewById(R.id.record_btn);
        mainLoader = findViewById(R.id.main_load);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
            }
        });

        viewPager2 = findViewById(R.id.viewpage2);
        myAdapter = new MyAdapter();
        viewPager2.setAdapter(myAdapter);
        viewPager2.setOffscreenPageLimit(5);

        getData();
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
                // 这个是网络请求的加载动画消失，不是加载封面图的动画消失
                mainLoader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<VideoResponse>> call, Throwable t) {
                Log.d("retrofit",t.getMessage());
                Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            }
        });

    }
}