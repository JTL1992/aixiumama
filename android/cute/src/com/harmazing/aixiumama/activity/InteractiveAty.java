package com.harmazing.aixiumama.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.harmazing.aixiumama.fragment.InteractiveFragment;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2014/12/8.
 */
public class InteractiveAty extends FragmentActivity {
    private Fragment mContent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_interactive);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        LogUtil.v("时间格式", str);
        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if (mContent == null) {
            mContent = new InteractiveFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame,mContent).commit();
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