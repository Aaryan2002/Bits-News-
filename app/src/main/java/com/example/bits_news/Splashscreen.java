package com.example.bits_news;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Splashscreen extends AppCompatActivity {

    ImageView imageView;
    TextView tv1,tv2;
    long animTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        imageView = (ImageView) findViewById(R.id.splash_logo);
        tv1 = (TextView) findViewById(R.id.splash_tv);
        tv2 = (TextView) findViewById(R.id.splash_tv2);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",400f);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(tv1,"x",200f);
        animatorY.setDuration(animTime);
        animatorX.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorX);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
}