package com.example.administrator.gifdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.gifdemo.view.GifView;

public class MainActivity extends AppCompatActivity {
    private GifView gif1, gif2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gif1 = (GifView) findViewById(R.id.gif1);
        // 设置背景gif图片资源
        gif1.setMovieResource(R.raw.cat);
        gif2 = (GifView) findViewById(R.id.gif2);
        gif2.setMovieResource(R.raw.yellow);
        // 设置暂停
        // gif2.setPaused(true);
    }
}
