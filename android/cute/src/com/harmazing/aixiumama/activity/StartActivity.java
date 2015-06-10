package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.BitmapUtil;
import com.harmazing.aixiumama.utils.UserInfoUtils;
import com.harmazing.aixiumama.model.sina.AccessTokenKeeper;
import com.harmazing.aixiumama.model.sina.Constants;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.view.BannerView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by liujinghui on 11/1/14.
 */
public class StartActivity extends Activity {

    public static QQAuth mQQAuth;
    private Tencent mTencent;
//      public static String APP_ID = "1104074570";//cute
//    public static String APP_ID="1103396138";
    public static String APP_ID = "1104440143";//aixiumama
    private IWXAPI api;
    String TAG = "StartActivity";
    BannerView bannerView;
    public static LoginButton weibo;
    IUiListener listener;
    public  AuthListener mLoginListener = new AuthListener();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        MobclickAgent.openActivityDurationTrack(false);//放弃使用友盟的自动统计功能，手动统计fragment页面
        bannerView=(BannerView) findViewById(R.id.bannerview1);
//        findViewById(R.id.qq).setOnClickListener(onClickListener);
//        findViewById(R.id.weixin).setOnClickListener(onClickListener);
//        findViewById(R.id.weibo).setOnClickListener(onClickListener);
        regToWeiXin();
        if(isFirstLogin()) {
            //初始化第三方登录
//            regToWeiXin();
            sinaInit();
            qqInit();
        }else{
            Intent intent = new Intent(StartActivity.this,TabHostActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.qq).setOnClickListener(onClickListener);
        findViewById(R.id.weixin).setOnClickListener(onClickListener);
        findViewById(R.id.weibo).setOnClickListener(onClickListener);
    }

    private boolean isFirstLogin() {
        return AppSharedPref.newInstance(getApplicationContext()).getUserId() == null;
    }

    public void sinaInit() {
        // 创建授权认证信息
        WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        weibo = (LoginButton) findViewById(R.id.weibo);
        weibo.setWeiboAuthInfo(authInfo, mLoginListener);
        Log.v("&&&&",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken()+"@");
    }

    /**
     *  不加这个 sina 授权回调不执行！！
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (weibo != null) {
            weibo.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void regToWeiXin(){
        api = WXAPIFactory.createWXAPI(this,CuteApplication.APP_ID,true);
        api.registerApp(CuteApplication.APP_ID);
        Log.v("微信初始化","");
    }
    private void qqInit() {
        mQQAuth = QQAuth.createInstance(APP_ID, this.getApplicationContext());
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        LogUtil.i(TAG, "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i(TAG, "isSessionValid=>" + mQQAuth.isSessionValid());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }

    private void qqLogin() {
        if (!mQQAuth.isSessionValid()) {
           listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    ToastUtil.show(StartActivity.this, "QQ 登录成功");
                    LogUtil.i(TAG, "登录成功=>" + values.toString());
                    LogUtil.i(TAG, "isSessionValid=>" + mQQAuth.isSessionValid());
                    LogUtil.i(TAG, "AccessToken=>" + mTencent.getAccessToken());
                    LogUtil.i(TAG, "QQToken=>" + mQQAuth.getQQToken());



                    /*
                           登陆成功给和美服务器
                           1.qq_openid： qq openid
                            2.qq_openkey: qq openkey
                            3.qq_sig: qq sig（需使用非URL编码）
                     */
                    RequestParams params = new RequestParams();
                    try {
                        params.put("qq_openid",values.getString("openid"));
                        params.put("qq_openkey",mTencent.getAccessToken());
//                        params.put("qq_openkey",mQQAuth.getQQToken());
                        AppSharedPref.newInstance(getApplicationContext()).setQQToken(mTencent.getAccessToken());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUtil.post(API.POST_QQ,params,new JsonHttpResponseHandler(){

                        ProgressDialog progressDialog = new ProgressDialog(StartActivity.this);
                        @Override
                        public void onStart() {
                            progressDialog.setMessage("正在获取个人信息...");
                            progressDialog.show();
                            super.onStart();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.v(TAG,"和美服务器发送请求成功");
                            ToastUtil.show(StartActivity.this, "和美服务器发送请求成功");
                            try {
                                progressDialog.dismiss();
                                String userId = response.getString("id");
                                AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                                AppSharedPref.newInstance(getApplicationContext()).setUserId(userId);
                                AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));


                                Log.v("token",response.getString("token")+"");
                                Log.v("id",response.getString("id")+"");
                                Log.v("username",response.getString("username")+"");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /**
                             *  获取个人信息 检查babies ??
                             */
                           HttpUtil.addClientHeader(getApplicationContext());

                            Log.v("获取个人信息Url",API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() +"/?format=json");
                            HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        /**
                                         * 存储个人信息
                                         */
                                        ToastUtil.show(StartActivity.this, "获取个人信息成功");
                                        saveWheelImages(response);
                                        saveUserInformation(response);
//                                        User.setDescription(getApplicationContext(),response.getString("description"));
//                                        User.setFansCount(getApplicationContext(),response.getInt("fans_count"));
//                                        User.setFollowsCount(getApplicationContext(),response.getInt("follows_count"));
                                        UserInfoUtils.setUserNmae(getApplicationContext(), response.getString("name"));
//                                        User.setVisitCount(getApplicationContext(),response.getInt("visited_count"));
//                                        User.setLikedCount(getApplicationContext(),response.getInt("liked_count"));
                                        AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(response.getString("weibo_token"));
                                        AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("qq_openid"));
                                        AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(response.getString("wechat_token"));
                                        AppSharedPref.newInstance(getApplicationContext()).setPicDir(response.getString("image"));

                                        String log = "weibo_token"+response.getString("weibo_token")+"qq_openid"+response.getString("qq_openid")+"wechat_token"+response.getString("wechat_token");
                                         Log.v("用户的绑定信息",log);

                                        //保存头像icon到本地
                                        saveThumbNail(response.getString("image_small"));                             

                                        JSONArray jsonArray = new JSONArray(response.getString("babies"));
                                        // 没有baby
                                        if(jsonArray.length() < 1){
                                            startActivity(new Intent(StartActivity.this, UserProfileStart.class));
                                            finish();
                                        }else{
                                            JSONObject obj = (JSONObject)jsonArray.get(0);
                                            UserInfoUtils.setBabyInfo(getApplication(), CuteApplication.calculateBabyInfo(obj));
                                            Log.v("obj",obj.toString());
                                            Log.v("BabyInfo",CuteApplication.calculateBabyInfo(obj));
                                            UserInfoUtils.setBirthday(getApplicationContext(), obj.getString("birthday"));
                                            UserInfoUtils.setSex(getApplicationContext(), obj.getInt("gender"));
                                            AppSharedPref.newInstance(getApplicationContext()).setBirthday(obj.getString("birthday"));
                                            AppSharedPref.newInstance(getApplicationContext()).setSexBaby(obj.getInt("gender"));
                                            startActivity(new Intent(StartActivity.this, TabHostActivity.class));
                                            finish();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    ToastUtil.show(StartActivity.this, "获取个人信息失败");
                                    LogUtil.e(TAG, responseString);

                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            });

                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            ToastUtil.show(StartActivity.this,"和美服务器发送请求失败");
                            LogUtil.e(TAG,responseString);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });


                }
            };
            //mQQAuth.login(this, "all", listener);
            mTencent.loginWithOEM(this, "all", listener, "10000144", "10000144", "xxxx");
        } else {

            //mQQAuth.logout(this);
        }
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        bannerView.bannerStartPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        bannerView.bannerStopPlay();
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {
        }
        @Override
        public void onError(UiError e) {
            LogUtil.i(TAG, "登录失败=>" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            LogUtil.i(TAG, "取消登录");
        }
    }

    
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.qq) {
                qqLogin();
            } else if (id == R.id.weixin) {
//                Intent intent = new Intent(StartActivity.this, WXEntryActivity.class);
//                StartActivity.this.startActivity(intent);
                loginWeiXin();
            } else if (id == R.id.weibo) {
                weibo.loginWeiBo();
            }
        }
    };

    private void loginWeiXin(){
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "cute";
        api.sendReq(req);
    }


    /**
     * 登入按钮的监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
       final   Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            /*
                    授权成功
             */
            if (accessToken != null && accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(accessToken.getExpiresTime()));
                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);

                AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);

                Log.v(TAG,"微博登陆成功"+String.format(format, accessToken.getToken(), date) +"@"+accessToken.getExpiresTime()+"@"+System.currentTimeMillis()+"@"+(System.currentTimeMillis()-accessToken.getExpiresTime()));

                /*
                           登陆成功给和美服务器
                           1.weibo_id：   uid
                           2.weibo_token  access_token
                     */
                RequestParams params = new RequestParams();
                try {
                    params.put("weibo_id", accessToken.getUid());
                    params.put("weibo_token", accessToken.getToken());
                    Log.v("weiboToken@@@",accessToken.getToken()+"@"+accessToken.getUid());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.post(API.POST_WEIBO,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.v(TAG,"和美服务器发送请求成功");
                        Log.v("startAtyRespon",response.toString());
                        try {
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
                            AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                            AppSharedPref.newInstance(getApplicationContext()).setUserId(response.getString("id"));
                            AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(response.getString("weibo_token"));
                            AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("qq_openid"));
                            AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(response.getString("wechat_token"));
                            String log = "weibo_token"+response.getString("weibo_token")+"qq_openid"+response.getString("qq_openid")+"wechat_token"+response.getString("wechat_token");
                            Log.v("用户的绑定信息",log);
                            Log.v("token",response.getString("token")+"");
                            Log.v("id",response.getString("id")+"");
                            Log.v("username",response.getString("username")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /**
                         *  获取个人信息 检查babies ??
                         */
                        HttpUtil.addClientHeader(getApplicationContext());

                        Log.v("获取个人信息Url",API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() +"/?format=json");
                        HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {
                            ProgressDialog progressDialog = new ProgressDialog(StartActivity.this);
                            @Override
                            public void onStart() {
                                progressDialog.setMessage("正在获取个人信息...");
                                progressDialog.show();
                                super.onStart();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    progressDialog.dismiss();
                                    /**
                                     * 存储个人信息
                                     */
                                     saveWheelImages(response);
                                    saveUserInformation(response);

                                    //  用户姓名
                                    UserInfoUtils.setUserNmae(getApplicationContext(), response.getString("name"));
                                    //  头像url
                                    UserInfoUtils.setIconUrl(getApplicationContext(), response.getString("image"));
                                    AppSharedPref.newInstance(getApplicationContext()).setPicDir(response.getString("image"));
                                    //保存头像icon到本地
                                    saveThumbNail(response.getString("image_small"));

                                    JSONArray jsonArray = new JSONArray(response.getString("babies"));
                                    // 没有baby
                                    if(jsonArray.length() < 1){
                                        startActivity(new Intent(StartActivity.this, UserProfileStart.class));
                                        finish();
                                    }else{
                                        //  baby信息，如 两个月  男  只读取第一个了
                                        JSONObject obj = (JSONObject)jsonArray.get(0);
                                        UserInfoUtils.setBabyInfo(getApplication(), CuteApplication.calculateBabyInfo(obj));
                                        Log.v("BabyInfo",CuteApplication.calculateBabyInfo(obj));
                                        UserInfoUtils.setBirthday(getApplicationContext(), obj.getString("birthday"));
                                        UserInfoUtils.setSex(getApplicationContext(), obj.getInt("gender"));
                                        AppSharedPref.newInstance(getApplicationContext()).setBirthday(obj.getString("birthday"));
                                        AppSharedPref.newInstance(getApplicationContext()).setSexBaby(obj.getInt("gender"));
                                        startActivity(new Intent(StartActivity.this, TabHostActivity.class));
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                super.onSuccess(statusCode, headers, response);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                ToastUtil.show(StartActivity.this, "获取个人信息失败");
                                LogUtil.e(TAG, responseString);

                                super.onFailure(statusCode, headers, responseString, throwable);
                            }
                        });
            }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });






            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(StartActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(StartActivity.this);
//                        mTokenView.setText(R.string.weibosdk_demo_logout_success);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
//            mTokenView.setText(R.string.weibosdk_demo_logout_failed);
        }
    }

    public void saveThumbNail(String url){
        if (CuteApplication.imageLoader != null)
        CuteApplication.imageLoader.loadImage(API.STICKERS + url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.v(TAG,"onLoadingStarted");
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                  LogUtil.v(TAG,"头像获取失败");
            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                Log.v(TAG,"开始保存user头像");
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            BitmapUtil.saveBitmap2sd(bitmap,AppSharedPref.newInstance(getApplicationContext()).getUserId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.run();
                    }
                }.start();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                LogUtil.v(TAG,"Cancelled");
            }
        });
    }
    public void saveWheelImages(JSONObject response){
        try {
        JSONObject wheelImages = response.getJSONObject("wheel_images");
        AppSharedPref.newInstance(getApplicationContext()).set1WheelImage(wheelImages.getString("first"));
        AppSharedPref.newInstance(getApplicationContext()).set2WheelImage(wheelImages.getString("second"));
        AppSharedPref.newInstance(getApplicationContext()).set3WheelImage(wheelImages.getString("third"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void saveUserInformation(JSONObject response){
        try {
            UserInfoUtils.setUserNmae(getApplicationContext(), response.getString("name"));
            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(response.getString("weibo_token"));
            AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("qq_openid"));
            AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(response.getString("wechat_token"));
            AppSharedPref.newInstance(getApplicationContext()).setPicDir(response.getString("image"));
            AppSharedPref.newInstance(getApplicationContext()).setRongToken(response.getString("rong_token"));
            String log = "weibo_token"+response.getString("weibo_token")+"qq_openid"+response.getString("qq_openid")+"wechat_token"+response.getString("wechat_token")+"rong_token"+response.getString("rong_token");
            LogUtil.v("用户的绑定信息",log);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

