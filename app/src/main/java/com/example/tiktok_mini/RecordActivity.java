package com.example.tiktok_mini;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class RecordActivity extends AppCompatActivity {

    private final int RECORD_PERMISSION = 1;
    private final int REQUEST_VIDEO_CAPTURE = 2;

    private Button redoBtn;
    private Button uploadBtn;
    private VideoView recordView;
    private Uri videoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        recordView = findViewById(R.id.record_view);
        redoBtn = findViewById(R.id.re_record_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        uploadBtn.setOnClickListener(view ->
            Toast.makeText(RecordActivity.this, "暂未开放", Toast.LENGTH_SHORT).show()
        );
        redoBtn.setOnClickListener(view -> callRecorder());
        checkPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recordView.start();
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (!(ActivityCompat.checkSelfPermission(RecordActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(RecordActivity.this, permissions[1]) == PackageManager.PERMISSION_GRANTED)
                || !(ActivityCompat.checkSelfPermission(RecordActivity.this, permissions[2]) == PackageManager.PERMISSION_GRANTED)) {
            // 未获得权限就要申请
            ActivityCompat.requestPermissions(RecordActivity.this, permissions, RECORD_PERMISSION);
        } else {
            callRecorder();
        }
    }

    private void callRecorder() {
        // 有两处调用：检查有权限调用，检查没权限但申请权限成功后调用
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode是用来识别是哪次申请权限
        if (requestCode == RECORD_PERMISSION && grantResults.length == 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            callRecorder();
        } else {
            Toast.makeText(RecordActivity.this, "权限未获得，无法使用录制功能", Toast.LENGTH_SHORT).show();
            RecordActivity.this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(RecordActivity.this, "视频已保存到系统相册", Toast.LENGTH_SHORT).show();
            videoUri = data.getData();
            recordView.setVideoURI(videoUri);
            recordView.start();
        } else {
            RecordActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordView = null;
    }
}
