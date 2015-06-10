package com.harmazing.aixiumama.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.harmazing.aixiumama.application.CuteApplication;
import com.harmazing.aixiumama.utils.AppSharedPref;
import com.harmazing.aixiumama.utils.HttpUtil;
import com.harmazing.aixiumama.utils.LogUtil;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.wxapi.WXEntryActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2015/1/4.
 */
public class PresentActivity extends Activity {
    WebView web;
    QQShare mQQShare;
    Tencent mTencent;
    QQAuth mQQAuth;
    Activity activity = this;
    String userId;
    private IWXAPI api;
//    public static String APP_ID="1103396138";
    public static JSONArray kindArray;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_layout);
        qqInit();
        api = WXAPIFactory.createWXAPI(this, CuteApplication.APP_ID);
       userId = AppSharedPref.newInstance(getApplicationContext()).getUserId();
        web = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = web.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        web.loadUrl("http://www.ourcute.com/list.php?uid="+userId);
        //设置Web视图
        web.setWebViewClient(new webViewClient());
    }
    private void qqInit() {

        mQQAuth = QQAuth.createInstance(StartActivity.APP_ID, this);
        mTencent = Tencent.createInstance(StartActivity.APP_ID, this);
        LogUtil.i("mQQAuth", "mQQAuth=>" + mQQAuth + ", mTencent=>" + mTencent);
        LogUtil.i("mQQAuth", "isSessionValid=>" + mQQAuth.isSessionValid());

        mQQShare = new QQShare(this, mQQAuth.getQQToken());
//        if (mQQAuth!=null && mQQAuth.isSessionValid()) {
//            startActivity(new Intent(this, UserProfileStart.class));
//            finish();
//        }
    }
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            int pid = 0;
            int uid = 0;
            int cid = 0;
            String shareUrl = null;
            String message = null;
            String title  = null;
            String summary = null;
            String message1 = null;
            LogUtil.v("url",url);
            if(url.equals("http://www.ourcute.com/index.html"))
              finish();
            else if (url.contains("cute_post")){
                String s = url.substring(38);
                String s1[] = s.split("&");
                for (int i = 0; i < s1.length; i++){
                    LogUtil.v("s1"+"[]"+i,s1[i]);
                    String id []= s1[i].split("=");
                    if (i == 0){
                        uid = Integer.parseInt(id[1]);
                        LogUtil.v("uid",""+uid);
                    }
                    if (i == 1){
                        pid = Integer.parseInt(id[1]);
                        LogUtil.v("pid",""+pid);
                    }
                    if (i == 2){
                      message1 = id[1];
                        LogUtil.v("message1",message1);
                        try {
                             message = URLDecoder.decode(message1,"UTF-8");
                             LogUtil.v("message",message);
                             String u[] = message.split("h");
                             for (int j = 0; j < u.length; j++){
                                 LogUtil.v("u"+j,u[j]);
                             }
                             shareUrl = "h"+u[1];
                             title = u[0];
                             summary = u[0];
                             LogUtil.v("message1"+"@url",message+"@"+shareUrl);
                        }catch (Exception e){
                            LogUtil.v("UTF-8 decode error","@@@@@@@");
                        }

                    }
                    if (i == 3){
                        cid = Integer.parseInt(id[1]);
                        LogUtil.v("cid",cid+"");
                    }
                    }
                LogUtil.v("s",s);
                if (cid == 1){//QQ分享
                    onQQShare(title,summary,shareUrl,pid,uid);
                }
                if (cid == 2){
                    wechatShare(title,summary,shareUrl,pid,uid,"person");
                }
                if (cid == 3){
                    wechatShare(title,summary,shareUrl,pid,uid,"FriendsCircle");
                }
            }
            else
             view.loadUrl(url);
            return true;
        }
    }
    private void onQQShare(String title,String summary, String url,final int pid,final int uid){
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.baidu.com");
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,getPicUrl());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "返回CUTE");

        new Thread(new Runnable() {
            @Override
            public void run() {
                mQQShare.shareToQQ(activity, params, new IUiListener(){
                    @Override
                    public void onComplete(Object o) {
                        LogUtil.v("o", o.toString());
                        RequestParams params1 = new RequestParams();
                        params1.put("pid",pid);
                        params1.put("uid",uid);
                        params1.put("type",1);
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

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(UiError uiError) {
                        LogUtil.v("uiError",uiError.errorMessage);
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mTencent != null)
            mTencent.onActivityResult(requestCode,resultCode,data);
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
        else {
            Toast.makeText(getApplicationContext(), "当前无内存卡，分享图片等功能无效！", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    private void wechatShare(String title,String summary, String url,final int pid,final int uid,String type){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = summary;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.thumbData = com.tencent.mm.sdk.platformtools.Util.bmpToByteArray(thumb, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage")+"&"+pid+"&"+uid+"&"+type;
        req.message = msg;
        req.scene = type.equals("FriendsCircle") ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        WXEntryActivity.webPageTag = req.transaction;
        LogUtil.v("WXEntryActivity.webPageTag",WXEntryActivity.webPageTag);
        api.sendReq(req);
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
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
