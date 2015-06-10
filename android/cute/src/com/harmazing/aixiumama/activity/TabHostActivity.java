package com.harmazing.aixiumama.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.fragment.MessageFragment;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.fragment.CameraFragment;
import com.harmazing.aixiumama.fragment.DiscoveryFragment;
import com.harmazing.aixiumama.fragment.HomeFragment;

import com.harmazing.aixiumama.fragment.UserFragment2;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by GeekClan
 * Email: 844842748@qq.com
 */
public class TabHostActivity extends FragmentActivity  {

    private static FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    Tencent mTencent;
    QQAuth mQQAuth;
    Button redButton;
    LoginButton weibo;
    ImageView redPoint;
    SlidingDrawer mSlidingDrawer;
    BroadcastReceiver receiver;
    public static int newMessageNum = 0;
    public static int cuteHaveRead = 0;
    public static int messageHaveRead = 0;
    private final String MY_ACTION = "com.cute.broadcast";
    private static final String APPKEY = "FyBMZaTfjiev6L45q9ROgZvR";
//    public static String APP_ID="1103396138";
    public static int PHOTO = 1;
    public static int CAMERA = 2;
    private long firstTime = 0;
    private final int NEW_RECOMMEND = 1;
    private final int NEW_MESSAGE = 2;
    private final int NEW_CUTE = 3;
    private final int CUTE_HAVE_READ = 4;
    private final int MESSAGE_HAVE_READ = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabhost);
//        if (!PushManager.isPushEnabled(this))
            LogUtil.v("执行了",PushManager.isPushEnabled(this));
        LogUtil.v("API KEY", getMetaValue(TabHostActivity.this, "api_key"));
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, getMetaValue(TabHostActivity.this, "api_key"));
        CuteApplication.activityList.add(this);
        startService(new Intent(getApplicationContext(),CuteService.class));
        weibo = (LoginButton) findViewById(R.id.weibo);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.sliding);
        qqInit();
        LogUtil.v("device_id&mac",getDeviceInfo(this));
        LogUtil.v(" Screen Height", CuteApplication.getScreenHW(getApplicationContext())[1]);
        LogUtil.v(" Screen Width", BitmapUtil.px2dip(getApplicationContext(),CuteApplication.getScreenHW(getApplicationContext())[0]));
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabWidget = mTabHost.getTabWidget();
//        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String s) {
//                ToastUtil.show(getApplicationContext(),s);
//            }
//        });
        setupFragment();
        //荣云连接。
        final String token = AppSharedPref.newInstance(this).getRongToken();
        if (!token.equals(""))
            rongConnect(token);
//           try {
//                RongIM.connect(token, new RongIMClient.ConnectCallback() {
//                   @Override
//                   public void onSuccess(String s) {
//                    // 此处处理连接成功。
//                    Log.d("此处处理连接成功Connect:", "Login successfully.");
//                   }
//
//                   @Override
//                   public void onError(ErrorCode errorCode) {
//                    // 此处处理连接错误。
//                    Log.d("此处处理连接错误Connect:", "Login failed." + errorCode.toString());
//                       if (errorCode == ErrorCode.TOKEN_INCORRECT){
//                           getNewRongToken();
//                       }
//                   }
//                });
//           }catch (Exception e){
//            e.printStackTrace();
//         }

//        com.harmazing.cute.service.BasicPushNotificationBuilder mBuilder  = new com.harmazing.cute.service.BasicPushNotificationBuilder(getApplicationContext());
//        mBuilder.setNotificationDefaults(Notification.DEFAULT_ALL);
//        mBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        mBuilder.construct(getApplicationContext());
//        PushManager.setDefaultNotificationBuilder(getApplication(), mBuilder);
//         com.harmazing.cute.service.BasicPushNotificationBuilder mBuilder  = new com.harmazing.cute.service.BasicPushNotificationBuilder(getApplication());
//         mBuilder.setNotificationFlags(Notification.FLAG_NO_CLEAR);
//         mBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
//         PushManager.setDefaultNotificationBuilder(this,mBuilder);
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == NEW_RECOMMEND){
                    redPoint.setVisibility(View.VISIBLE);
                }
                if (msg.what == NEW_MESSAGE){
//                  newMessageNum += msg.arg1;
                    newMessageNum = CuteService.newMessageCount - TabHostActivity.messageHaveRead - TabHostActivity.cuteHaveRead;
                    MessageFragment.newCuteNum = CuteService.newCutedCount - TabHostActivity.cuteHaveRead;
                    MessageFragment.newMessageNum = newMessageNum - MessageFragment.newCuteNum;
                    if (newMessageNum > 0){
                        redButton.setText(""+newMessageNum);
                        redButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        redButton.setVisibility(View.INVISIBLE);
                    }
                }
                if (msg.what == NEW_CUTE){
//                    MessageFragment.newMessageNum = newMessageNum - MessageFragment.newCuteNum;
                    MessageFragment.newCuteNum = CuteService.newCutedCount - TabHostActivity.cuteHaveRead;
                    LogUtil.v("newCute",MessageFragment.newCuteNum);
                }
            }
        };
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MY_ACTION.equals(intent.getAction())){

                    Log.i("获取广播","com.cute.broadcast"+intent.getIntExtra("action",0));
                    int action = intent.getIntExtra("action",0);
                    if (action == NEW_RECOMMEND){
                       mHandler.sendEmptyMessage(action);
                        Log.i("获取推荐和关注广播","com.cute.broadcast");
                    }
                    if (action == NEW_MESSAGE){
                        Message message = new Message();
                        message.what = action;
                        message.arg1 = intent.getIntExtra("count",1);
                        mHandler.sendMessage(message);
                        Log.i("获取新消息广播","com.cute.broadcast");
                    }
                    if (action == NEW_CUTE) {
                        Message message = new Message();
                        message.what = action;
                        message.arg1 = intent.getIntExtra("count", 1);
//                        MessageFragment.newCuteNum  += intent.getIntExtra("count", 1);
                        mHandler.sendMessage(message);
                        Log.i("获取cute数", "" + MessageFragment.newCuteNum);
                    }
                    if (action == CUTE_HAVE_READ){
                        Message message = new Message();
                        message.what = NEW_MESSAGE;
                        LogUtil.v("CUTE_HAVE_READ",CUTE_HAVE_READ);
                        mHandler.sendMessage(message);
                    }
                    if (action == MESSAGE_HAVE_READ){
                        Message message = new Message();
                        message.what = NEW_MESSAGE;
                        LogUtil.v("MESSAGE_HAVE_READ",MESSAGE_HAVE_READ);
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.cute.broadcast");
        registerReceiver(receiver,filter);
    }

    public static void setCurFragment(int i){
//        mTabHost.setCurrentTab(i);

        LogUtil.v("before",mTabHost.getCurrentTab()+"");
        mTabHost.setCurrentTab(i);
        LogUtil.v("after",mTabHost.getCurrentTab()+"");
    }

    private void setupFragment(){
        mTabHost.addTab(createTab("1", "首页", R.drawable.tab_c), HomeFragment.class, null);
        mTabHost.addTab(createTab("2", "发现", R.drawable.tab_discovery), DiscoveryFragment.class, null);
        mTabHost.addTab(createTab("3", "拍照", R.drawable.index_main), CameraFragment.class, null);
        mTabHost.addTab(createTab("4", "消息", R.drawable.tab_msg), MessageFragment.class, null);
        mTabHost.addTab(createTab("5", "个人中心", R.drawable.tab_person), UserFragment2.class, null);

        mTabWidget.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(TabHostActivity.this,ActivityGallery.class);
                Dialog alertDialog = new AlertDialog.Builder(TabHostActivity.this)
                        .setItems(new String[]{"相册","拍照"}, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.gc();
                                System.runFinalization();
                                if(which == 0){
                                    intent.putExtra("action",PHOTO);
                                }else if(which == 1){
                                    intent.putExtra("action",CAMERA);
                                }
                                startActivity(intent);
                                //  切换到关注页看我发布的内容
                                TabHostActivity.setCurFragment(0);
                                HomeFragment.setCurItem(1);
                            }
                        }).create();
                alertDialog.show();
            }
        });
    }
    private TabHost.TabSpec createTab(String tag, String tabName, Integer resId) {
        return mTabHost.newTabSpec(tag).setIndicator(getTabView(resId, tabName));
    }

    private View getTabView(int resId, String tabName){
        View view = View.inflate(this, R.layout.bottom_tabs_item, null);

        ImageView tabImg = (ImageView) view.findViewById(R.id.bottom_tabs_item_img);
        tabImg.setImageResource(resId);
        if (tabName.equals("首页")){
            redPoint = (ImageView) view.findViewById(R.id.red_point);
        }
        if (tabName.equals("消息")){
            redButton = (Button) view.findViewById(R.id.red_button);
        }
        return view;
    }
    private void qqInit() {
            mTencent = Tencent.createInstance(StartActivity.APP_ID, this);
            LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
//            LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());
        }

//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTencent.onActivityResult(requestCode, resultCode, data);
        if (weibo !=null)
        weibo.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_BACK) {
              if (mSlidingDrawer.isOpened())
                mSlidingDrawer.animateClose();

              long secondTime = System.currentTimeMillis();
              if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                  Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                  firstTime = secondTime;//更新firstTime
                  return true;
              } else {                                                    //两次按键小于2秒时，退出应用
                  System.exit(0);
                  MobclickAgent.onKillProcess(this);
              }
          }
        return false;
    }
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        newMessageNum = 0;
        cuteHaveRead = 0;
        messageHaveRead = 0;
        MessageFragment.newCuteNum = 0;
        MessageFragment.newMessageNum = 0;
//        CuteApplication.black1_alpha = null;
//        CuteApplication.black1_1 = null;
//        CuteApplication.black2_1 = null;
//        CuteApplication.black2_alpha = null;
//        CuteApplication.white_anim1 = null;
//        CuteApplication.white_anim2 = null;
//        CuteApplication.white_anim3 = null;
//        CuteApplication.gifFromResource.recycle();
//        CuteApplication.imageLoader.destroy();
        System.gc();
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }

            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void getNewRongToken(){
        HttpUtil.get(API.GET_USER+AppSharedPref.newInstance(this).getUserId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AppSharedPref.newInstance(TabHostActivity.this).setRongToken(response.getString("rong_token"));
                    String token = AppSharedPref.newInstance(TabHostActivity.this).getRongToken();
                    if (!token.equals(""))
                    rongConnect(token);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void rongConnect(String token){
        try {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onSuccess(String s) {
                    // 此处处理连接成功。
                    Log.d("此处处理连接成功Connect:", "Login successfully.");
                }

                @Override
                public void onError(ErrorCode errorCode) {
                    // 此处处理连接错误。
                    Log.d("此处处理连接错误Connect:", "Login failed." + errorCode.toString());
                    if (errorCode == ErrorCode.TOKEN_INCORRECT){
                        getNewRongToken();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}