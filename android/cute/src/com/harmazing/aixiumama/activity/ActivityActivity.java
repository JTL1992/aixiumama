package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;

/**
 * Created by Administrator on 2015/3/24.
 */
public class ActivityActivity extends Activity {
    private final String TAG = "ActivityActivity";
    WebView web;
    private final int ACTIVITY_1 = 1;
    private final int ACTIVITY_2 = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_layout);
        web = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = web.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        int type = getIntent().getExtras().getInt("ACTIVITY");//1:轮播1 3：轮播3
        String birthday = CuteApplication.date2Age(AppSharedPref.newInstance(this).getBirthday());
        String age = "";
        if (birthday.contains("岁")){
            if (Integer.parseInt(birthday.split("岁")[0]) > 4)
                age = "5";
            else{
                if (birthday.split("岁")[1].contains("0"))
                     age = birthday.split("岁")[0];
                else
                     age = String.valueOf(Integer.parseInt(birthday.split("岁")[0])+1);
                }
        }
        else if (birthday.contains("预产期"))
             age = "0";
        else
             age = "1";
        LogUtil.v(TAG+"age",age+"@"+type);
        if (type == ACTIVITY_1)
              web.loadUrl("http://www.ourcute.com/sale.php?age=" + age);
        else
              web.loadUrl("http://www.ourcute.com/list.php? age=" + age);
        //设置Web视图
        web.setWebViewClient(new webViewClient());
    }
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("back"))
                finish();
            return true;
        }
    }
}