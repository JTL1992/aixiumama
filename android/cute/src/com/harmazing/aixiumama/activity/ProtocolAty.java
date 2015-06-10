package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.harmazing.aixiumama.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by JTL on 2014/11/26.
 * 用户协议
 */
public class ProtocolAty extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        ImageView btnBack = (ImageView) findViewById(R.id.title_protocol).findViewById(R.id.left_view);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}