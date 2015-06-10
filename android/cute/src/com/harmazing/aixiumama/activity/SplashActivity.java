package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.AppSharedPref;

/**
 * Created by Administrator on 2015/3/24.
 */
public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        if (AppSharedPref.newInstance(getApplicationContext()).getSplashFlag()){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppSharedPref.newInstance(getApplicationContext()).setSplashFlag(false);
                Intent intent = new Intent(SplashActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
        }else{
            Intent intent = new Intent(SplashActivity.this,StartActivity.class);
            startActivity(intent);
            finish();
        }
    }
}