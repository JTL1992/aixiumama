package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.harmazing.aixiumama.API.API;
import com.harmazing.aixiumama.API.HttpHead;
import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.fragment.MessageFragment;
import com.harmazing.aixiumama.model.sina.AccessTokenKeeper;
import com.harmazing.aixiumama.model.sina.Constants;
import com.harmazing.aixiumama.model.sina.LoginButton;
import com.harmazing.aixiumama.service.CuteService;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.utils.ToastUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.view.SlipButton;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;

import org.apache.http.Header;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JTL on 2014/11/25.
 * 个人中心设置
 */
public class UserSettingAty extends BaseActivity {
    public static QQAuth mQQAuth;
    private Tencent mTencent;
    //  public static String APP_ID = "222222";
//    public static String APP_ID="1103396138";
    TextView txtName;
    SlidingDrawer mSlidingDrawer;
    RelativeLayout bindLayout;
    RelativeLayout shareLayout;
    LoginButton bindWeibo;
    ImageView imageName;
    String userName;
    String userImage;
    private String userBirthday;
    ProgressDialog pBar;
    private IWXAPI api;
    int progress;
    String TAG = "UserSettingAty";
    private static int MAX_PROGRESS = 100;
    private final String customerServiceId = "KEFU1425968915331";
    private AuthListener mLoginListener = new AuthListener();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID);
        sinaInit();
        qqInit();
        Bundle bundle = getIntent().getExtras();
        userBirthday = bundle.getString("birthday");
        userName = bundle.getString("name");
        userImage = bundle.getString("image");
        //UI组件
        txtName = (TextView) findViewById(R.id.text_name);
        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);

        imageName = (ImageView) findViewById(R.id.image_name);
        SlipButton save = (SlipButton) findViewById(R.id.baocun).findViewById(R.id.save);
        RelativeLayout name = (RelativeLayout) findViewById(R.id.name);
        RelativeLayout interactive = (RelativeLayout) findViewById(R.id.hudong);
        RelativeLayout share = (RelativeLayout) findViewById(R.id.fenxiang);
        RelativeLayout invent = (RelativeLayout) findViewById(R.id.yaoqing);
        RelativeLayout platform = (RelativeLayout) findViewById(R.id.bangding);
        RelativeLayout notice = (RelativeLayout) findViewById(R.id.xinxiaoxi);
        RelativeLayout acquaintanceCute = (RelativeLayout) findViewById(R.id.liaojie);
        RelativeLayout likeCute = (RelativeLayout) findViewById(R.id.xihuan);
        RelativeLayout advice = (RelativeLayout) findViewById(R.id.yijian);
        RelativeLayout protocol = (RelativeLayout) findViewById(R.id.xieyi);
        RelativeLayout back = (RelativeLayout) findViewById(R.id.tuichu);
        RelativeLayout checkVersion = (RelativeLayout) findViewById(R.id.jiancha);
        bindLayout = (RelativeLayout) findViewById(R.id.bind);
        shareLayout = (RelativeLayout) findViewById(R.id.share);
        ImageView bindQQ = (ImageView) findViewById(R.id.image_qq_bind);
        bindWeibo = (LoginButton) findViewById(R.id.image_weibo_bind);
        ImageView bindWeixin = (ImageView) findViewById(R.id.image_weixin_bind);
        ImageView btnQQ = (ImageView) findViewById(R.id.qq);
        ImageView btnWeibo = (ImageView) findViewById(R.id.weibo);
        ImageView btnWeixin = (ImageView) findViewById(R.id.weixin);
        ImageView btnFriendcirle = (ImageView) findViewById(R.id.pengyouquan);
        RelativeLayout btnClearCach = (RelativeLayout) findViewById(R.id.qingchu);
        CuteApplication.downloadIamge(API.STICKERS + userImage, imageName);
        txtName.setText(userName);
         //UI组件添加监听器
//        findViewById(R.id.majia).setOnClickListener(onMajiaListener);
        findViewById(R.id.majia).setVisibility(View.GONE);
        checkVersion.setOnClickListener(onVerionListener);
        findViewById(R.id.back).setOnClickListener(onBackListener);
        interactive.setOnClickListener(onInteractiveListener);
        acquaintanceCute.setOnClickListener(onAcquaintanceCuteListener);
        likeCute.setOnClickListener(onLikeCuteListener);
        protocol.setOnClickListener(onProtocolListener);
        back.setOnClickListener(onBackOutListener);
        advice.setOnClickListener(onAdviceListener);
        notice.setOnClickListener(onNotifyListener);
        name.setOnClickListener(onNameListener);
        share.setOnClickListener(onShareListener);
        platform.setOnClickListener(onPlatformListener);
        btnWeibo.setOnClickListener(onWeiboListener);
        bindWeibo.setOnClickListener(onBindWeiboListener);
        bindQQ.setOnClickListener(onBindQQListener);
        invent.setOnClickListener(onInviteListener);
        btnQQ.setOnClickListener(onQQListener);
        btnClearCach.setOnClickListener(onClearCachListener);
        save.setOnChangedListener(onSaveChangedListener);
        btnFriendcirle.setOnClickListener(onWeiXinFriendsCircleListener);
        btnWeixin.setOnClickListener(onWeiXinPersonListener);
        bindWeixin.setOnClickListener(onBindWeChatListener);

        /**
         * rongyu test
         */

        // 用来实现 UI 线程的更新。
        final Handler mHandler;
//        mHandler = new Handler();
//        findViewById(R.id.lianxikefu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        RongIM.getInstance().startCustomerServiceChat(UserSettingAty.this, customerServiceId, "在线客服");
//                    }
//                });
//            }
//        });



        if (CuteApplication.getIsSave())
            save.setCheck(true);
        else
            save.setCheck(false);
        LogUtil.v("savepic", CuteApplication.getIsSave() + "");
        if (CuteApplication.provinces.isEmpty())
         HttpUtil.get(API.CURSOR_CITY, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    for (int i = 0; i < response.length(); i++) {
                        HashMap<Integer, String> proName = new HashMap<Integer, String>();
                        HashMap<Integer, Integer> areaName = new HashMap<Integer, Integer>();
                        HashMap<Integer, String> idName = new HashMap<Integer, String>();
                        LogUtil.v(" response.length()", "" + response.length());
                        String name = response.getJSONObject(i).getString("name");
                        int id = response.getJSONObject(i).getInt("id");
                        LogUtil.v("response.getJSONObject(i).getInt(\"id\"):", "" + response.getJSONObject(i).getInt("id"));
                        CuteApplication.provinces.put(id, name);

                        JSONArray array = response.getJSONObject(i).getJSONArray("cities");
                        LogUtil.v("array", array.toString());
                        for (int j = 0; j < array.length(); j++) {
                            String city = array.getJSONObject(j).getString("name");
                            int cityId = array.getJSONObject(j).getInt("id");
                            proName.put(j, city);
                            areaName.put(j, cityId);
                            idName.put(cityId,city);
                        }
//                        sort(proName);
                        CuteApplication.locations.put(id, proName);
                        CuteApplication.areaId.put(id,areaName);
                        CuteApplication.findCity.put(id,idName);
                        LogUtil.v("shengid",""+id);
                        LogUtil.v("findCity", CuteApplication.findCity.get(id).toString());
                        LogUtil.v("CuteApplication.areaId",CuteApplication.areaId.get(id).toString());
//                 proName.clear();
                    }
//                sort(provinces);
//                    CuteApplication.provinces = (HashMap<Integer,String>) provinces.clone();
//                    CuteApplication.areaId = (HashMap<Integer,Integer>)areaId.clone();
//                    CuteApplication.locations = (HashMap<Integer,HashMap<Integer,String>>) locations.clone();
                } catch (Exception e) {
                    LogUtil.v("JSON", "解析出错");
                }

            }
        });
    }
    View.OnClickListener onWeiXinPersonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        wechatShare("wechat_person");
        }
    };
    View.OnClickListener onBindWeChatListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (AppSharedPref.newInstance(UserSettingAty.this).getWeichatToken() == null)
            bindWeChat();
            else
            Toast.makeText(UserSettingAty.this,"您的微信账号已绑定！",Toast.LENGTH_LONG).show();
            mSlidingDrawer.animateClose();
        }
    };
    View.OnClickListener onVerionListener = new View.OnClickListener() {
        String version = "";
        String apkUrl = "";
        String description = "";
        String name = "";
        PackageInfo info;
        @Override
        public void onClick(View view) {
            final ProgressDialog Bar = new ProgressDialog(UserSettingAty.this);
            Bar.setTitle("正在监测");
            Bar.setMessage("请稍后。。。");
            Bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            Bar.show();
            try{
              info = getPackageManager().getPackageInfo(getPackageName(), 0);
            }catch (Exception e){
                e.printStackTrace();
            }
            // 当前应用的版本名称
            final String versionName = info.versionName;
            HttpUtil.get(API.VERSION_CHECK,new JsonHttpResponseHandler(){
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                   Bar.dismiss();
                   super.onSuccess(statusCode, headers, response);
                    try {
                        version = response.getString("version");
                        apkUrl = response.getString("apk");
                        description = response.getString("description");
                        name = response.getString("name");
                    }catch (Exception e ){
                        e.printStackTrace();
                    }
                  if (!versionName.equals(version)){
                      Dialog update = new AlertDialog.Builder(UserSettingAty.this).setTitle("检查到新版本:"+name+"是否更新?").setMessage("新版本特性："+description).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {

                          }
                      }).setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              pBar = new ProgressDialog(UserSettingAty.this);
                              pBar.setTitle("正在下载");
                              pBar.setMessage("请稍后。。。");
                              pBar.setCancelable(true);
                              pBar.setIndeterminate(false);
                              pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                              pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                              pBar.setMax(MAX_PROGRESS);
                              pBar.setButton("取消", new DialogInterface.OnClickListener()
                              {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which)
                                  {
                                       pBar.dismiss();
                                  }
                              });
                              pBar.show();
                              HttpUtil.get(HttpHead.head+apkUrl,new FileAsyncHttpResponseHandler(getApplicationContext()) {
                                  @Override
                                  public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                                          Toast.makeText(getApplicationContext(),"下载失败，请重新尝试",Toast.LENGTH_LONG).show();
                                          pBar.dismiss();
                                  }

                                  @Override
                                  public void onSuccess(int i, Header[] headers, File file) {
                                      LogUtil.v("file",file.getAbsolutePath());
                                      pBar.dismiss();
                                      Intent intent = new Intent(Intent.ACTION_VIEW);
                                      intent.setDataAndType(Uri.fromFile(file),
                                              "application/vnd.android.package-archive");
                                      startActivity(intent);
//
                                  }

                                  @Override
                                  public void onProgress(final int bytesWritten, final int totalSize) {
                                      super.onProgress(bytesWritten, totalSize);

                                      float written  = (float) bytesWritten;
                                      float size = (float) totalSize;
                                      progress = (int)(100*(written/size));

                                      pBar.setProgress(progress);
                                      LogUtil.v("%%%%%",""+bytesWritten+"@"+totalSize+"@"+progress+"@"+bytesWritten/totalSize);


                              }
                              });
                          }
                      }).create();
                   update.show();
                  }
                   else{
                      Toast.makeText(getApplicationContext(),"暂无新版本",Toast.LENGTH_LONG).show();
                  }
               }
           });
        }
    };

    SlipButton.OnChangedListener onSaveChangedListener = new SlipButton.OnChangedListener() {
        @Override
        public void OnChanged(boolean CheckState) {
                CuteApplication.setSavePic(CheckState);
            if (CheckState){
               Toast.makeText(getApplicationContext(),"发布的图片将保存到SDcard/Pictures/cute",Toast.LENGTH_LONG).show();
            }
        }
    };
   View.OnClickListener onWeiXinFriendsCircleListener = new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           wechatShare("FriendsCircle");
       }
    };
    View.OnClickListener onMajiaListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(),MajiaAty.class);
            startActivity(intent);

        }
    };
    View.OnClickListener onInteractiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           Intent intent = new Intent(getApplicationContext(),InteractiveAty.class);
            startActivity(intent);

        }
    };
    View.OnClickListener onQQListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Bundle params = new Bundle();
            ArrayList<String> imageUrls = new ArrayList<String>();
            imageUrls.add(getPicUrl());
            String title = "如果你是一位宝妈或怀孕中,想不想参考同龄宝宝们都在干啥？";
            String summery = "来吧，我也晒了这么多!";
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,summery );
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, API.SHARE_TO_FRIENDS+AppSharedPref.newInstance(getApplicationContext()).getUserId()+"/");
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrls);
            doShareToQzone(params);
            mSlidingDrawer.animateClose();
        }
    };
    View.OnClickListener onBindQQListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (AppSharedPref.newInstance(getApplicationContext()).getQQToken().equals(""))
                qqLogin();
            else
                Toast.makeText(getApplicationContext(),"您的QQ账号已经绑定",Toast.LENGTH_LONG).show();
            mSlidingDrawer.animateClose();
        }
    };
    View.OnClickListener onInviteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), InviteFriendsAty.class);
            mSlidingDrawer.close();
            startActivity(intent);
        }
    };
    View.OnClickListener onBindWeiboListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (AppSharedPref.newInstance(getApplicationContext()).getWeiboToken().equals("")) {
                bindWeibo.loginWeiBo();

            }
            else
                Toast.makeText(getApplicationContext(),"您的微博账户已经绑定",Toast.LENGTH_LONG).show();
            mSlidingDrawer.animateClose();
        }

    };
    View.OnClickListener onPlatformListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mSlidingDrawer.close();
            if (!mSlidingDrawer.isOpened()){
//                mSlidingDrawer.animateOpen();
                shareLayout.setVisibility(View.INVISIBLE);
                bindLayout.setVisibility(View.VISIBLE);
                mSlidingDrawer.animateOpen();
            }
            else
                mSlidingDrawer.animateClose();
//            shareLayout.setVisibility(View.INVISIBLE);
//            bindLayout.setVisibility(View.VISIBLE);
        }
    };
    View.OnClickListener onWeiboListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSlidingDrawer.animateClose();
            RequestParams p = new RequestParams();
            LogUtil.v("access_token@@@",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
            if (AppSharedPref.newInstance(getApplicationContext()).getWeiboToken().equals("")){
                Toast.makeText(getApplicationContext(),"您的微博没有绑定，请先微博登录或绑定！",Toast.LENGTH_LONG).show();
                bindWeibo.loginWeiBo();
            }
            else{
                p.put("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
                HttpUtil.post(API.ACCESS_TOKEN_IS_VALID,p,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            int time = response.getInt("expire_in");
                            LogUtil.v("time!!!",time+"");
                            if (time > 0){
                                shareToWeibo();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"您的微博认证已经过期，请重新微博登录或绑定！",Toast.LENGTH_LONG).show();
                                bindWeibo.loginWeiBo();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
//                        LogUtil.v("失败","@@@@@@");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        LogUtil.v("失败","@@@@@@");
                        Toast.makeText(getApplicationContext(),"您的微博认证已经过期，请重新微博登录或绑定！",Toast.LENGTH_LONG).show();
                        bindWeibo.loginWeiBo();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        LogUtil.v("失败","@@@@@@");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtil.v("!!!!!!!!!!!!!!!","结束" +
                                "");
                    }
                });
            }
//            String status = null;
////            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
//            try {
//                status = URLEncoder.encode("我在用CUTE！想说的都在照片里，小伙伴们快来吧！@CUTE");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            RequestParams params = new RequestParams();
//            params.put("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
//            params.put("status",status);
//            params.put("pic",new ByteArrayInputStream(getImageByte(R.drawable.ic_launcher)),"title.png");
//            HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params,new JsonHttpResponseHandler(){
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);
//                    LogUtil.v("response", response.toString());
//                    Toast.makeText(getApplicationContext(),"分享主页成功！",Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    super.onFailure(statusCode, headers, responseString, throwable);
//                    LogUtil.v("errer",responseString);
//                }
//            });
        }
    };
    View.OnClickListener onShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!mSlidingDrawer.isOpened()){
//            mSlidingDrawer.animateOpen();
                shareLayout.setVisibility(View.VISIBLE);
                bindLayout.setVisibility(View.INVISIBLE);
                mSlidingDrawer.animateOpen();
            }
            else
                mSlidingDrawer.animateClose();

        }
    };
    View.OnClickListener onClearCachListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog clearCachDialog = new AlertDialog.Builder(UserSettingAty.this).setMessage("根据缓存大小，清理时间从几秒到几分钟不等，请耐心等待").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setPositiveButton("清除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CuteApplication.clearCache();
                    CuteService.deleteStickersCach();
                    Toast.makeText(getApplicationContext(), "缓存清理完毕", Toast.LENGTH_LONG).show();
                }
            }).create();
            clearCachDialog.show();
        }

    };
    View.OnClickListener onBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    View.OnClickListener onAcquaintanceCuteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), AcquaintanceCuteAty.class);
            startActivity(intent);
            mSlidingDrawer.close();
        }
    };
    View.OnClickListener onLikeCuteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), LikeCuteAty.class);
            startActivity(intent);
            mSlidingDrawer.close();
        }
    };
    View.OnClickListener onProtocolListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ProtocolAty.class);
            startActivity(intent);
            mSlidingDrawer.close();
        }
    };
    View.OnClickListener onBackOutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            CuteApplication.ExitApplication();
            stopService(new Intent(CuteService.ACTION));
            TabHostActivity.newMessageNum = 0;
            TabHostActivity.cuteHaveRead = 0;
            TabHostActivity.messageHaveRead = 0;
            MessageFragment.newCuteNum = 0;
            MessageFragment.newMessageNum = 0;
            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken("");
            AppSharedPref.newInstance(getApplicationContext()).setWeichatToken(null);
            if (StartActivity.mQQAuth != null)
               if (StartActivity.mQQAuth.isSessionValid())
                  StartActivity.mQQAuth.logout(getApplicationContext());
            startActivity(intent);

//            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//            startActivity(intent);
             finish();
//            ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//            activityManager.restartPackage(getPackageName());
//            LogUtil.v("getPackageName()",getPackageName());
//            finish();
//            ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//            activityManager.restartPackage(getPackageName());
//            android.os.Process.killProcess(android.os.Process.myPid());
////            System.exit(0);
        }
    };
    View.OnClickListener onAdviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), AdviceAty.class);
            startActivity(intent);
            mSlidingDrawer.close();
        }
    };
    View.OnClickListener onNotifyListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), NotifyAty.class);
            mSlidingDrawer.close();
        startActivity(intent);
        }
    };
    View.OnClickListener onNameListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), EditPersonDataAty.class);
            intent.putExtra("birthday", userBirthday);
            intent.putExtra("name",userName);
            intent.putExtra("image",userImage);
            startActivityForResult(intent, 0);
            mSlidingDrawer.close();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            if (mSlidingDrawer.isOpened()){
                mSlidingDrawer.animateClose();
                 return true;}
            else
               return super.onKeyDown(keyCode,event);
        else
          return super.onKeyDown(keyCode,event);
    }
    private void sinaInit() {
        // 创建授权认证信息
        WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        bindWeibo = (LoginButton) findViewById(R.id.image_weibo_bind);
        bindWeibo.setWeiboAuthInfo(authInfo, mLoginListener);
    }
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
       final     Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            /*
                    授权成功
             */
            if (accessToken != null && accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(accessToken.getExpiresTime()));
                String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
//                mTokenView.setText(String.format(format, accessToken.getToken(), date));
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
                Toast.makeText(getApplicationContext(),"微博登录成功！",Toast.LENGTH_LONG).show();
                LogUtil.v(TAG,"微博登陆成功"+format);

                /*
                           登陆成功给和美服务器
                           1.weibo_id：   uid
                           2.weibo_token  access_token
                     */
                RequestParams params = new RequestParams();
                try {
                    params.put("weibo_id", accessToken.getUid());
                    params.put("weibo_token", accessToken.getToken());
                    LogUtil.v("weibo_id+weibo_token",accessToken.getUid()+"++"+accessToken.getToken());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
//                    AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpUtil.addClientHeader(getApplicationContext());
                HttpUtil.post(API.BIND_WEIBO, params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        LogUtil.v(TAG,"和美服务器发送请求成功");
                        try {
                            mSlidingDrawer.close();
                            Toast.makeText(getApplicationContext(),"绑定成功!!!!!!!",Toast.LENGTH_LONG).show();
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboToken(accessToken.getToken());
                            AppSharedPref.newInstance(getApplicationContext()).setWeiboUserId(accessToken.getUid());
//                            AppSharedPref.newInstance(getApplicationContext()).setUserName(response.getString("username"));

                            LogUtil.v("token",response.getString("token")+"");
                            LogUtil.v("id",response.getString("id")+"");
//                            mSlidingDrawer.close();
//                            Toast.makeText(getApplicationContext(),"绑定成功!!!!!!!",Toast.LENGTH_LONG).show();
//                            LogUtil.v("username",response.getString("username")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(),"您的微博账户已经绑定其他用户！",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtil.v("@@@@@@","!!!!!!!");
                    }
                });
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UserSettingAty.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(UserSettingAty.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == 0){
              if (data != null){
              userName = data.getExtras().getString("name");
              txtName.setText(data.getExtras().getString("name"));
              if (data.getExtras().get("image") != null){
              Drawable drawable = new BitmapDrawable((Bitmap)data.getExtras().get("image"));
              imageName.setImageDrawable(drawable);
              }
             }
          }
        if (bindWeibo!= null) {
            bindWeibo.onActivityResult(requestCode, resultCode, data);
        }
        if (mTencent!= null)
            mTencent.onActivityResult(requestCode,resultCode,data);
    }
    private void qqInit() {
        mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, this.getApplicationContext());
        mTencent = Tencent.createInstance(StartActivity.APP_ID, this.getApplicationContext());
        LogUtil.i(TAG, "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i(TAG, "isSessionValid=>" + mQQAuth.isSessionValid());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
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
    private void qqLogin() {
        if (!mQQAuth.isSessionValid()) {
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
//                    ToastUtil.show(UserSettingAty.this, "QQ 登录成功");
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
//                        params.put("qq_sig","yCdpibyLyps+wrZMat/4vqkC3cc=");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpUtil.addClientHeader(getApplicationContext());
                    HttpUtil.post(API.BIND_QQ,params,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            LogUtil.v(TAG,"和美服务器发送请求成功"+response.toString());
                            try {
                                mSlidingDrawer.close();
                                Toast.makeText(getApplication(),"绑定成功！",Toast.LENGTH_LONG).show();
                                AppSharedPref.newInstance(getApplicationContext()).setQQToken(response.getString("token"));
                                AppSharedPref.newInstance(getApplicationContext()).setQQUserId(response.getString("id"));

                                LogUtil.v("token",response.getString("token")+"");
                                LogUtil.v("id",response.getString("id")+"");
                                LogUtil.v("username",response.getString("username")+"");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//

                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            ToastUtil.show(UserSettingAty.this, "您的QQ号已经绑定了其他账户");
                            LogUtil.e(TAG,responseString);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }

                        @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                ToastUtil.show(UserSettingAty.this,"您的QQ号已经绑定了其他账户");
                            LogUtil.e(TAG,errorResponse.toString());
                            mQQAuth.logout(getApplicationContext());
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
    private void doShareToQzone(final Bundle params) {
        final Activity activity = this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mTencent.shareToQzone(activity, params, new IUiListener() {

                    @Override
                    public void onCancel() {
                        LogUtil.v("activity", "onCancel: ");
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                        LogUtil.v("onError: " + e.errorMessage, "e");
                    }

                    @Override
                    public void onComplete(Object response) {
                        // TODO Auto-generated method stub
                        LogUtil.v("onComplete: ", response.toString());
                    }

                });
            }
        }).start();
    }
    public byte []  getImageByte(int ImageId){
        Resources res= this.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, ImageId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,baos);
        ByteArrayBody byteArrayBody = new ByteArrayBody(baos.toByteArray(),"invite.png");

        LogUtil.v("byte",baos.toByteArray().length+"");
        return baos.toByteArray();
    }
    private String getPicUrl() {
        Resources res = getApplicationContext().getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
        try {
            return saveBitmap(bmp);
        }catch (Exception e){
            return  null;
        }

    }
    public String saveBitmap(Bitmap bitmap) throws IOException
    {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
            .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if  (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            File file = new File(sdDir,"Pictures/logo.png");
            FileOutputStream out;
            try{
                out = new FileOutputStream(file);
                if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
                {
                    out.flush();
                    out.close();
                }
                LogUtil.v("存入内存卡",file.getAbsolutePath());
                return file.getAbsolutePath();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"当前无内存卡，分享图片等功能无效！",Toast.LENGTH_LONG).show();
            return null;
        }

    }
   public void isSDcard(){
       if (Environment.getExternalStorageState()
               .equals(Environment.MEDIA_MOUNTED))
           Toast.makeText(getApplicationContext(),"SD卡不存在，分享图片等功能可能受到影响！",Toast.LENGTH_LONG).show();
   }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        mSlidingDrawer.close();
    }
    private void shareToWeibo(){
        String status = null;
//            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        try {
            status = URLEncoder.encode("如果你是一位宝妈或怀孕中，想不想参考同龄宝宝们都在干啥？来吧，我也晒了这么多！");
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestParams params = new RequestParams();
        params.put("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
        params.put("status",status);
        params.put("pic",new ByteArrayInputStream(getImageByte(R.drawable.ic_launcher)),"title.JPEG");
        LogUtil.v("access_token",AppSharedPref.newInstance(getApplicationContext()).getWeiboToken());
        HttpUtil.post(API.SEND_MESSAGE_TO_WEIBO_FRIENDS, params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogUtil.v("response", response.toString());
                UMPlatformData platform = new UMPlatformData(UMPlatformData.UMedia.SINA_WEIBO, AppSharedPref.newInstance(UserSettingAty.this).getUserId());
                MobclickAgent.onSocialEvent(UserSettingAty.this, platform);
                Toast.makeText(getApplicationContext(),"分享主页成功！",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogUtil.v("errer",responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                LogUtil.v("error",errorResponse.toString());
                LogUtil.v("shibai",statusCode+"");
                Toast.makeText(getApplicationContext(),"由于网络原因，分享失败！",Toast.LENGTH_LONG).show();
            }
        });
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    private void wechatShare(String type){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = API.SHARE_TO_FRIENDS+AppSharedPref.newInstance(getApplicationContext()).getUserId()+"/";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "如果你是一位宝妈或怀孕中，想不想参考同龄宝宝们都在干啥？来吧，我也晒了这么多!";
        msg.description = "想来吧，我也晒了这么多!";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.thumbData = com.tencent.mm.sdk.platformtools.Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }
    private void bindWeChat() {
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "cute";
        api.sendReq(req);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
