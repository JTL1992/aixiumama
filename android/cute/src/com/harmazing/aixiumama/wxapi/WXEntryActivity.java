package com.harmazing.aixiumama.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.activity.UserProfileStart;
import com.harmazing.aixiumama.application.CuteApplication;


import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.utils.UserInfoUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/1/28.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    public static String webPageTag = "";
    public static String youMengShareTag = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list);
        regToWeiXin();
        api.handleIntent(getIntent(), this);
    }
    private void loginWeiXin(){
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "cute";
        api.sendReq(req);
    }
    private void regToWeiXin(){
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID, true);
        api.registerApp(CuteApplication.APP_ID);
        Log.v("微信初始化", "");
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                Toast.makeText(this,"成功",Toast.LENGTH_LONG).show();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                Toast.makeText(this,"成功",Toast.LENGTH_LONG).show();
                Log.v("goToShowMsg((ShowMessageFromWX.Req) baseReq);","");
                break;
            default:
                break;
        }
    }


    @Override
    public void onResp(BaseResp baseResp){
        Bundle bundle = new Bundle();
        int result = 0;
        Log.v("onResp","onResp");
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                baseResp.toBundle(bundle);
                SendAuth.Resp resp = new SendAuth.Resp(bundle);
                String code = resp.token;
                int type = baseResp.getType();
                if (type == ConstantsAPI.COMMAND_SENDAUTH){
                    if (AppSharedPref.newInstance(WXEntryActivity.this).getBirthday().equals("")){
                      getToken(code,"login");
                        com.tencent.mm.sdk.platformtools.Log.v("执行登录请求","code:"+code);
                    }
                    else{
                      getToken(code,"bind");
                        com.tencent.mm.sdk.platformtools.Log.v("执行绑定请求","code:"+code);
                      finish();
                    }
//                        com.tencent.mm.sdk.platformtools.Log.v("token",token);
//                        loginHarmazing(token);
                }
                if (type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
                    Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
                    com.tencent.mm.sdk.platformtools.Log.v("bundle.toString()",bundle.toString()+"!");
                    com.tencent.mm.sdk.platformtools.Log.v("baseResp.transaction",baseResp.transaction);
                    if (baseResp.transaction.equals(webPageTag)){//礼品分享成功，发送消息
                            String s[] = baseResp.transaction.split("&");
                            int pid = Integer.parseInt(s[1]);
                            int uid = Integer.parseInt(s[2]);
                            int tid = s[3].equals("person") ? 1 : 1;
                        com.tencent.mm.sdk.platformtools.Log.v("s[1]&s[2]",s[1]+"&"+s[2]);
                             sendBackData2Ourcute(pid, uid, tid);
                        finish();
                    }
                    if (baseResp.transaction.equals(youMengShareTag)) {
                        UMPlatformData platform;

                        if (youMengShareTag.contains("FriendsCircle"))
                            platform = new UMPlatformData(UMPlatformData.UMedia.WEIXIN_CIRCLE, AppSharedPref.newInstance(WXEntryActivity.this).getUserId());
                            else
                            platform = new UMPlatformData(UMPlatformData.UMedia.WEIXIN_FRIENDS, AppSharedPref.newInstance(WXEntryActivity.this).getUserId());
                        MobclickAgent.onSocialEvent(this, platform);
                        finish();
                    }
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                finish();
                break;
            default:
                result = R.string.errcode_unknown;
                finish();
                break;
        }
//        Log.v("微信登陆成功","yeah");
//        Log.v("微信返回值",baseResp.toString());
//        Toast.makeText(this, getResources().getString(result), Toast.LENGTH_LONG).show();
//        finish();
    }
    private void loginHarmazing(String token,String id){
        RequestParams params = new RequestParams();
            params.put("wechat_id", id);
            params.put("wechat_token", token);
//            Log.v("wechatToken",token);
        HttpUtil.post(API.POST_WECHAT, params, new JsonHttpResponseHandler() {
             ProgressDialog progressDialog = new ProgressDialog(WXEntryActivity.this);

            @Override
            public void onStart() {
                progressDialog.setMessage("正在获取个人信息...");
                progressDialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                com.tencent.mm.sdk.platformtools.Log.v("WECHAT", "和美服务器发送请求成功");
                try {
                    progressDialog.dismiss();
                    AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                    AppSharedPref.newInstance(getApplicationContext()).setUserId(response.getString("id"));
                    AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));
                    com.tencent.mm.sdk.platformtools.Log.v("token", response.getString("token") + "");
                    com.tencent.mm.sdk.platformtools.Log.v("id", response.getString("id") + "");
                    com.tencent.mm.sdk.platformtools.Log.v("username", response.getString("username") + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /**
                 *  获取个人信息 检查babies ??
                 */
                HttpUtil.addClientHeader(getApplicationContext());

                com.tencent.mm.sdk.platformtools.Log.v("获取个人信息Url", API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "/?format=json");
                HttpUtil.get(API.GET_USER + AppSharedPref.newInstance(getApplicationContext()).getUserId() + "/?format=json", new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            /**
                             * 存储个人信息
                             */
                        saveWheelImages(response);
//                                        User.setDescription(getApplicationContext(),response.getString("description"));
//                                        User.setFansCount(getApplicationContext(),response.getInt("fans_count"));
//                                        User.setFollowsCount(getApplicationContext(),response.getInt("follows_count"));
                            UserInfoUtils.setUserNmae(getApplicationContext(), response.getString("name"));
//                                        User.setVisitCount(getApplicationContext(),response.getInt("visited_count"));
//                                        User.setLikedCount(getApplicationContext(),response.getInt("liked_count"));
//                            AppSharedPref.newInstance(getApplicationContext()).setToken(response.getString("token"));
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(response.getString("weibo_token"));
                            AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("qq_openid"));
                            AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(response.getString("wechat_token"));
                            AppSharedPref.newInstance(getApplicationContext()).setPicDir(response.getString("image"));
                            AppSharedPref.newInstance(getApplicationContext()).setRongToken(response.getString("rong_token"));
                            String log = "weibo_token" + response.getString("weibo_token") + "qq_openid" + response.getString("qq_openid") + "wechat_token" + response.getString("wechat_token");
                            com.tencent.mm.sdk.platformtools.Log.v("用户的绑定信息", log);


                            JSONArray jsonArray = new JSONArray(response.getString("babies"));
                            // 没有baby
                            if (jsonArray.length() < 1) {
                                startActivity(new Intent(WXEntryActivity.this, UserProfileStart.class));
                                finish();
                            } else {
                                JSONObject obj = (JSONObject) jsonArray.get(0);
                                UserInfoUtils.setBabyInfo(getApplication(), CuteApplication.calculateBabyInfo(obj));
                                Log.v("obj", obj.toString());
                                Log.v("BabyInfo", CuteApplication.calculateBabyInfo(obj));
                                UserInfoUtils.setBirthday(getApplicationContext(), obj.getString("birthday"));
                                UserInfoUtils.setSex(getApplicationContext(), obj.getInt("gender"));
                                AppSharedPref.newInstance(getApplicationContext()).setBirthday(obj.getString("birthday"));
                                AppSharedPref.newInstance(getApplicationContext()).setSexBaby(obj.getInt("gender"));
                                startActivity(new Intent(WXEntryActivity.this, TabHostActivity.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        ToastUtil.show(WXEntryActivity.this, "获取个人信息失败");
                        LogUtil.e("WECHAT", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        ToastUtil.show(WXEntryActivity.this, "获取个人信息失败");
                        LogUtil.e("WECHAT", responseString);

                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtil.show(WXEntryActivity.this, "和美服务器发送请求失败");
                progressDialog.dismiss();
                LogUtil.e("WECHAT", responseString);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }
    private void loginHarmazing(final String token,final String id,String type){
        RequestParams params = new RequestParams();
        params.put("wechat_id", id);
        params.put("wechat_token", token);
        HttpUtil.addClientHeader(WXEntryActivity.this);
        HttpUtil.post(API.BIND_WECHAT,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                com.tencent.mm.sdk.platformtools.Log.v("微信绑定成功", response.toString());
                Toast.makeText(getApplication(),"绑定成功！",Toast.LENGTH_LONG).show();
                AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(token);
                AppSharedPref.newInstance(getApplicationContext()).setWeichatUserId(id);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                com.tencent.mm.sdk.platformtools.Log.v("微信绑定失败","");
                ToastUtil.show(WXEntryActivity.this,"您的微信号已经绑定了其他账户");
            }
        });
    }
    private void getToken(String code,final String type){
        RequestParams params = new RequestParams();
        params.put("appid",CuteApplication.APP_ID);
        params.put("secret", CuteApplication.APP_SECREAT);
        params.put("code",code);
        params.put("grant_type","authorization_code");

        HttpUtil.get(API.WEI_XIN_GET_TOKEN,params,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        com.tencent.mm.sdk.platformtools.Log.v("WEI_XIN_GET_TOKEN",response.toString());
                        String accessToken = response.getString("access_token");
                        String openid = response.getString("openid");
                        if (type.equals("login")){
                            com.tencent.mm.sdk.platformtools.Log.v("@@@@@@@22","access_token:"+accessToken+"openid:"+openid);
                        loginHarmazing(accessToken,openid);
                        }
                        else
                        loginHarmazing(accessToken,openid,"bind");
//                        Toast.makeText(WXEntryActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


    }
    private void sendBackData2Ourcute(int pid,int uid,int tid){
        RequestParams params1 = new RequestParams();
        params1.put("pid",pid);
        params1.put("uid",uid);
        params1.put("type",tid);
        HttpUtil.post("http://www.ourcute.com/cute_config/order_add.php?", params1, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("JSON", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.v("JSON errorResponse",errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("responseString",responseString+statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogUtil.v("errorResponse",errorResponse.toString());
            }
        });
//                        web.loadUrl("http://yfbass.ourcute.linux2.jiuhost.com/cute_config/order_test.php?pid="+pid+"&uid="+uid+"&type=1");
        Toast.makeText(getApplicationContext(), "分享成功！", Toast.LENGTH_LONG).show();

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
}
