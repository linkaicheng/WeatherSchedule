package com.cheng.weatherschedule.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cheng.weatherschedule.MainActivity;
import com.cheng.weatherschedule.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        new Thread(){

            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                    Intent intent=new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
