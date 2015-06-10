package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import com.baidu.android.pushservice.PushManager;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.view.SlipButton;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2014/11/26.
 */
public class NotifyAty extends Activity {
    SlipButton btnVoice;
    SlipButton btnShake;
    SlipButton btnAvoid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        ImageView btnBack = (ImageView) findViewById(R.id.title_notify).findViewById(R.id.left_view);
        btnBack.setOnClickListener(onBackListener);
      final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      final Vibrator vibrator =(Vibrator)getSystemService(VIBRATOR_SERVICE);
        btnVoice = (SlipButton) findViewById(R.id.btn_voice);
        btnShake = (SlipButton) findViewById(R.id.btn_shake);
        btnAvoid = (SlipButton) findViewById(R.id.btn_avoid_noise);
        init();
        btnVoice.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if (CheckState){
                  am.setStreamVolume( AudioManager.STREAM_ALARM , 15, 0);
                  CuteApplication.isVoice = true;
                }
                else{
                  am.setStreamVolume( AudioManager.STREAM_ALARM , 0, 0);
                  CuteApplication.isVoice = false;
                }
            }
        });
        btnShake.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                   if (CheckState){
                       vibrator.vibrate(5000);
                       CuteApplication.isShake = true;
                   }
                    else{
                       vibrator.cancel();
                       CuteApplication.isShake = false;
                   }
            }
        });
        btnAvoid.setOnChangedListener(new SlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if (CheckState){
                    PushManager.setNoDisturbMode(getApplication(),22,0,8,0);
                    CuteApplication.isAvoid = true;
                }
                else{
                    PushManager.setNoDisturbMode(getApplication(),0,0,0,0);
                    CuteApplication.isAvoid = false;
                }
            }
        });
    }
    View.OnClickListener onBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    public void init(){
        if (CuteApplication.isVoice)
            btnVoice.setCheck(true);
        if (CuteApplication.isShake)
            btnShake.setCheck(true);
        if (CuteApplication.isAvoid){
            btnAvoid.setCheck(true);
            PushManager.setNoDisturbMode(getApplication(),22,0,8,0);}
        else{
            btnAvoid.setCheck(false);
        }
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